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

package net.sourceforge.htmlunit.xerces.impl.xpath;

/**
 * XPath exception.
 * 
 * @xerces.internal
 *
 * @author Andy Clark, IBM
 *
 * @version $Id$
 */
public class XPathException 
    extends Exception {

    /** Serialization version. */
    static final long serialVersionUID = -948482312169512085L;
    
    // Data

    // hold the value of the key this Exception refers to.
    private final String fKey;
    //
    // Constructors
    //

    /** Constructs an exception. */
    public XPathException() {
        super();
        fKey = "c-general-xpath";
    } // <init>()

    /** Constructs an exception with the specified key. */
    public XPathException(String key) {
        super();
        fKey = key;
    } // <init>(String)

    public String getKey() {
        return fKey;
    } // getKey():  String

} // class XPathException
