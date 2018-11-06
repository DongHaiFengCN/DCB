package com.example.ydd.common.dto;

public class BindDeviceResponse {

    public static final String BIND_DEVICE_RESPONSE_SUCCESS = "ok";


    /**
     * status : ok
     * msg : 登录成功！
     * data : {"channelId":"c5ab3fb5","auth":{"expiresIn":1541255961564,"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsSWQiOiJjNWFiM2ZiNSIsInVzZXJuYW1lIjoiMTU2ODg4ODI0ODciLCJpYXQiOjE1NDEyMTI3NjEsImV4cCI6MTU4NDQxMjc2MX0.FutdeZFdzRoI6vWLM9xvlqx81Y1PkrHGw8vkdoFXug4"}}
     */

    private String status;
    private String msg;
    private DataBean data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * channelId : c5ab3fb5
         * auth : {"expiresIn":1541255961564,"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsSWQiOiJjNWFiM2ZiNSIsInVzZXJuYW1lIjoiMTU2ODg4ODI0ODciLCJpYXQiOjE1NDEyMTI3NjEsImV4cCI6MTU4NDQxMjc2MX0.FutdeZFdzRoI6vWLM9xvlqx81Y1PkrHGw8vkdoFXug4"}
         */

        private String channelId;
        private AuthBean auth;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public AuthBean getAuth() {
            return auth;
        }

        public void setAuth(AuthBean auth) {
            this.auth = auth;
        }

        public static class AuthBean {
            /**
             * expiresIn : 1541255961564
             * token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsSWQiOiJjNWFiM2ZiNSIsInVzZXJuYW1lIjoiMTU2ODg4ODI0ODciLCJpYXQiOjE1NDEyMTI3NjEsImV4cCI6MTU4NDQxMjc2MX0.FutdeZFdzRoI6vWLM9xvlqx81Y1PkrHGw8vkdoFXug4
             */

            private long expiresIn;
            private String token;

            public long getExpiresIn() {
                return expiresIn;
            }

            public void setExpiresIn(long expiresIn) {
                this.expiresIn = expiresIn;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }
        }
    }
}
