package com.muzlive.kitpage.kitpage.config.logging;

import io.micrometer.core.instrument.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.ReadListener;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;

@Slf4j
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

    private byte[] bodyData;

    private Charset encoding;

    private String charsetName;

    private String contentType;

    private final Map<String, ArrayList<String>> parameters = new LinkedHashMap<String, ArrayList<String>>();

    ByteChunk tmpName = new ByteChunk();

    ByteChunk tmpValue = new ByteChunk();

    private int DEFAULT_BUFFER_SIZE = 4096;

    private boolean parsed = false;

    public HttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        InputStream inputStream = super.getInputStream();
        bodyData = IOUtils.toByteArray(inputStream);
        charsetName = request.getCharacterEncoding();
        if(StringUtils.isBlank(charsetName)){
            charsetName = StandardCharsets.UTF_8.name();
        }
        this.encoding = Charset.forName(charsetName);
        contentType = request.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(bodyData);
        return new ServletInputStream(bis);
    }

    @Override
    public String getParameter(String name){
        parseParameters();

        ArrayList<String> values = this.parameters.get(name);
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    @Override
    public HashMap<String, String[]> getParameterMap() {
        parseParameters();
        HashMap<String, String[]> map = new HashMap<String, String[]>(this.parameters.size() * 2);
        for (String name : this.parameters.keySet()) {
            ArrayList<String> values = this.parameters.get(name);
            map.put(name, values.toArray(new String[values.size()]));
        }
        return map;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Enumeration<String>() {
            private String[] arr = (String[])(getParameterMap().keySet().toArray(new String[0]));
            private int index = 0;

            @Override
            public boolean hasMoreElements() {
                return index < arr.length;
            }

            @Override
            public String nextElement() {
                return arr[index++];
            }
        };
    }

    @Override
    public String[] getParameterValues(String name) {
        parseParameters();

        ArrayList<String> values = this.parameters.get(name);
        if (values == null) {
            return null;
        }

        String[] arr = values.toArray(new String[values.size()]);
        if (arr == null) {
            return null;
        }
        return arr;
    }

    private void parseGetParameters() {
        Map<String, String[]> map = super.getParameterMap();
        for (String name : map.keySet()) {
            String[] values = map.get(name);
            parameters.put(name, new ArrayList<String>(Arrays.asList(values)));
        }
    }

    private void parseParameters() {


        if (super.getMethod().equalsIgnoreCase("GET")) {
            parseGetParameters();
            return;
        }

        if (super.getMethod().equalsIgnoreCase("DELETE")) {
            parseGetParameters();
            return;
        }

        if (parsed) {
            return;
        } else {
            parsed = true;
        }

        if (!("application/x-www-form-urlencoded".equalsIgnoreCase(super.getContentType()))) {
            return;
        }

        int pos = 0;
        int end = this.bodyData.length;

        while (pos < end) {
            int nameStart = pos;
            int nameEnd = -1;
            int valueStart = -1;
            int valueEnd = -1;

            boolean parsingName = true;
            boolean decodeName = false;
            boolean decodeValue = false;
            boolean parameterComplete = false;

            do {
                switch (this.bodyData[pos]) {
                    case '=':
                        if (parsingName) {
                            // Name finished. Value starts from next character
                            nameEnd = pos;
                            parsingName = false;
                            valueStart = ++pos;
                        } else {
                            // Equals character in value
                            pos++;
                        }
                        break;
                    case '&':
                        if (parsingName) {
                            // Name finished. No value.
                            nameEnd = pos;
                        } else {
                            // Value finished
                            valueEnd = pos;
                        }
                        parameterComplete = true;
                        pos++;
                        break;
                    case '%':
                    case '+':
                        // Decoding required
                        if (parsingName) {
                            decodeName = true;
                        } else {
                            decodeValue = true;
                        }
                        pos++;
                        break;
                    default:
                        pos++;
                        break;
                }
            } while (!parameterComplete && pos < end);

            if (pos == end) {
                if (nameEnd == -1) {
                    nameEnd = pos;
                } else if (valueStart > -1 && valueEnd == -1) {
                    valueEnd = pos;
                }
            }

            if (nameEnd <= nameStart) {
                continue;
                // ignore invalid chunk
            }

            tmpName.setByteChunk(this.bodyData, nameStart, nameEnd - nameStart);
            if (valueStart >= 0) {
                tmpValue.setByteChunk(this.bodyData, valueStart, valueEnd - valueStart);
            } else {
                tmpValue.setByteChunk(this.bodyData, 0, 0);
            }

            try {
                String name;
                String value;

                if (decodeName) {
                    name = new String(URLCodec.decodeUrl(Arrays.copyOfRange(tmpName.getBytes(), tmpName.getStart(), tmpName.getEnd())), this.encoding);
                } else {
                    name = new String(tmpName.getBytes(), tmpName.getStart(), tmpName.getEnd() - tmpName.getStart(), this.encoding);
                }

                if (valueStart >= 0) {
                    if (decodeValue) {
                        value = new String(URLCodec.decodeUrl(Arrays.copyOfRange(tmpValue.getBytes(), tmpValue.getStart(), tmpValue.getEnd())), this.encoding);
                    } else {
                        value = new String(tmpValue.getBytes(), tmpValue.getStart(), tmpValue.getEnd() - tmpValue.getStart(), this.encoding);
                    }
                } else {
                    value = "";
                }

                if (StringUtils.isNotBlank(name)) {
                    ArrayList<String> values = this.parameters.get(name);
                    if (values == null) {
                        values = new ArrayList<String>(1);
                        this.parameters.put(name, values);
                    }
                    if (StringUtils.isNotBlank(value)) {
                        values.add(value);
                    }
                }
            } catch (DecoderException e) {
                log.error(e.getStackTrace().toString());
                // ignore invalid chunk
            }

            tmpName.recycle();
            tmpValue.recycle();
        }
    }

    public String getBody() throws IOException {
        int i;
        StringBuffer sb = new StringBuffer();
        InputStream inputStream = this.getInputStream();
        if (StringUtils.isNotEmpty(contentType)  && contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            byte[] boundary = contentType.substring(contentType.lastIndexOf("boundary=") + "boundary=".length(), contentType.length()).getBytes();
            MultipartStream multipartStream = new MultipartStream(inputStream, boundary, DEFAULT_BUFFER_SIZE, null);

            boolean nextPart = multipartStream.skipPreamble();
            while (nextPart) {
                String header = multipartStream.readHeaders();
                sb.append("Headers:").append(header);
                String contentType = header.substring(header.lastIndexOf("Content-Type:") + "Content-Type:".length(), header.length()).trim();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                multipartStream.readBodyData(output);
                if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
                    sb.append("-BODY : " + new String(output.toByteArray(),"UTF-8"));
                }
                nextPart = multipartStream.readBoundary();
            }
        } else {
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];

            while ((i = inputStream.read(bytes)) != -1) {
                String line = new String(bytes, 0, i, charsetName);
                sb.append(line);
            }
        }

        if (StringUtils.isNotEmpty(sb.toString())) {
            return Pattern.compile("\\s").matcher(sb.toString()).replaceAll("");
        }
        return null;
    }
}

class ServletInputStream extends javax.servlet.ServletInputStream {

    private InputStream is;

    public ServletInputStream(InputStream bis) {
        is = bis;
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return is.read(b);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        //
    }
}

class ByteChunk {

    private byte[] buff;
    private int start = 0;
    private int end;

    public void setByteChunk(byte[] b, int off, int len) {
        buff = b;
        start = off;
        end = start + len;
    }

    public byte[] getBytes() {
        return buff;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void recycle() {
        buff = null;
        start = 0;
        end = 0;
    }
}
