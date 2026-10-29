package com.aip.queue.config;

/** Minimal HTML escaping helper for user-entered values rendered in JSP pages. */
public final class Html {
    private Html() {
    }

    public static String escape(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}
