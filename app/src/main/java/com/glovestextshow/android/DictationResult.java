package com.glovestextshow.android;

import java.util.List;

public class DictationResult {
    private String sn;
    private String is;
    private String bg;
    private String ed;

    private List<Words> ws;

    public static class Words{
        private String bg;
        private List<Cw> cw;

        public static class Cw{
            private String sc;
            private String w;

            public String getSc() {
                return sc;
            }

            public void setSc(String sc) {
                this.sc = sc;
            }

            public String getW() {
                return w;
            }

            public void setW(String w) {
                this.w = w;
            }

            @Override
            public String toString() {
                return w;
            }
        }

        public String getBg() {
            return bg;
        }

        public void setBg(String bg) {
            this.bg = bg;
        }

        public List<Cw> getCw() {
            return cw;
        }

        public void setCw(List<Cw> cw) {
            this.cw = cw;
        }

        @Override
        public String toString() {
            String result = "";
            for (Cw cwTmp: cw) {
                result += cwTmp.toString();
            }
            return result;
        }

    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getIs() {
        return is;
    }

    public void setIs(String is) {
        this.is = is;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getEd() {
        return ed;
    }

    public void setEd(String ed) {
        this.ed = ed;
    }

    public List<Words> getWs() {
        return ws;
    }

    public void setWs(List<Words> ws) {
        this.ws = ws;
    }

    @Override
    public String toString() {
        String result = "";
        for(Words wordsTmp : ws){
            result += wordsTmp.toString();
        }
        return result;
    }
}
