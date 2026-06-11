package com.jsonparser.writer;

public final class WriteOptions {

    public static final WriteOptions COMPACT = new WriteOptions(false, "  ");
    public static final WriteOptions PRETTY  = new WriteOptions(true,  "  ");

    private final boolean prettyPrint;
    private final String  indent;

    private WriteOptions(boolean prettyPrint, String indent) {
        this.prettyPrint = prettyPrint;
        this.indent = indent;
    }

    public boolean isPrettyPrint() { return prettyPrint; }
    public String  getIndent()     { return indent; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private boolean prettyPrint = true;
        private String  indent      = "  ";

        public Builder prettyPrint(boolean prettyPrint) {
            this.prettyPrint = prettyPrint;
            return this;
        }

        /** 인덴트 단위 문자열 (기본값: 공백 2칸) */
        public Builder indent(String indent) {
            this.indent = indent;
            return this;
        }

        public WriteOptions build() {
            return new WriteOptions(prettyPrint, indent);
        }
    }
}
