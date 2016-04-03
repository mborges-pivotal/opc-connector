package io.pivotal.tola;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class OpcSourceProperties {

    private String _clsId;
    private String _progId;
    private String _host;
    private String _domain;
    private String _user;
    private String _password;
    private String[] _tags;

    public String getClsId() {
        return _clsId;
    }

    public void setClsId(String clsId) {
        _clsId = clsId;
    }

    public String getProgId() {
        return _progId;
    }

    public void setProgId(String progId) {
        _progId = progId;
    }

    public String getHost() {
        return _host;
    }

    public void setHost(String host) {
        _host = host;
    }

    public String getDomain() {
        return _domain;
    }

    public void setDomain(String domain) {
        _domain = domain;
    }

    public String getUser() {
        return _user;
    }

    public void setUser(String user) {
        _user = user;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public String[] getTags() {
        return _tags;
    }

    public void setTags(String[] tags) {
        _tags = tags;
    }
}
