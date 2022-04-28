package io.appactive.rpc.springcloud.common;

/**
 *
 */
public class ServiceMeta implements Comparable<ServiceMeta>{

    private String uriPrefix;
    private Label labels;

    public ServiceMeta() {
    }

    public ServiceMeta(String uriPrefix, Label labels) {
        this.uriPrefix = uriPrefix;
        this.labels = labels;
    }

    public ServiceMeta(String uriPrefix, String writeMode) {
        this.uriPrefix = uriPrefix;
        this.labels = new Label(writeMode);
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public Label getLabels() {
        return labels;
    }

    public void setLabels(Label labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "ServiceMeta{" +
                "uriPrefix='" + uriPrefix + '\'' +
                ", labels=" + labels +
                '}';
    }

    @Override
    public int compareTo(ServiceMeta o) {
        int pre = this.uriPrefix.compareTo(o.getUriPrefix());
        return pre == 0 ? this.labels.compareTo(o.getLabels()) : pre;
    }


    public static class Label implements Comparable<Label>{
        private String writeMode;

        public Label() {
        }

        public Label(String writeMode) {
            this.writeMode = writeMode;
        }

        public String getWriteMode() {
            return writeMode;
        }

        public void setWriteMode(String writeMode) {
            this.writeMode = writeMode;
        }

        @Override
        public String toString() {
            return "Label{" +
                    ", writeMode='" + writeMode + '\'' +
                    '}';
        }

        @Override
        public int compareTo(Label o) {
            return this.getWriteMode().compareTo(o.getWriteMode());
        }
    }
}
