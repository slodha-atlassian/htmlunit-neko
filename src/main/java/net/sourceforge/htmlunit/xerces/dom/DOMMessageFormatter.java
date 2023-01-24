/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.htmlunit.xerces.dom;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Used to format DOM error messages.
 * <p>
 *
 * @author Sandy Gao, IBM
 */
public class DOMMessageFormatter {

    public static final String DOM_DOMAIN = "http://www.w3.org/dom/DOMTR";
    public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";

    private static ResourceBundle domResourceBundle = null;
    private static ResourceBundle xmlResourceBundle = null;

    DOMMessageFormatter() {
    }

    /**
     * Formats a message with the specified arguments.
     *
     * @param domain    domain from which error string is to come.
     * @param key       The message key.
     * @param arguments The message replacement text arguments. The order of the
     *                  arguments must match that of the placeholders in the actual
     *                  message.
     *
     * @return the formatted message.
     *
     * @throws MissingResourceException Thrown if the message with the specified key
     *                                  cannot be found.
     */
    public static String formatMessage(String domain, String key, Object[] arguments) throws MissingResourceException {
        ResourceBundle resourceBundle = getResourceBundle(domain);
        if (resourceBundle == null) {
            init();
            resourceBundle = getResourceBundle(domain);
            if (resourceBundle == null)
                throw new MissingResourceException("Unknown domain" + domain, null, key);
        }
        // format message
        String msg;
        try {
            msg = key + ": " + resourceBundle.getString(key);
            if (arguments != null) {
                try {
                    msg = java.text.MessageFormat.format(msg, arguments);
                } catch (Exception e) {
                    msg = resourceBundle.getString("FormatFailed");
                    msg += " " + resourceBundle.getString(key);
                }
            }
        } // error
        catch (MissingResourceException e) {
            msg = resourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(key, msg, key);
        }

        // no message
        if (msg == null) {
            msg = key;
            if (arguments.length > 0) {
                StringBuilder str = new StringBuilder(msg);
                str.append('?');
                for (int i = 0; i < arguments.length; i++) {
                    if (i > 0) {
                        str.append('&');
                    }
                    str.append(arguments[i]);
                }
                msg = str.toString();
            }
        }

        return msg;
    }

    static ResourceBundle getResourceBundle(String domain) {
        if (domain == DOM_DOMAIN || domain.equals(DOM_DOMAIN)) {
            return domResourceBundle;
        } else if (domain == XML_DOMAIN || domain.equals(XML_DOMAIN)) {
            return xmlResourceBundle;
        }
        return null;
    }

    /**
     * Initialize Message Formatter.
     */
    public static void init() {
        domResourceBundle = ResourceBundle.getBundle("net.sourceforge.htmlunit.xerces.impl.msg.DOMMessages");
        xmlResourceBundle = ResourceBundle.getBundle("net.sourceforge.htmlunit.xerces.impl.msg.XMLMessages");
    }
}
