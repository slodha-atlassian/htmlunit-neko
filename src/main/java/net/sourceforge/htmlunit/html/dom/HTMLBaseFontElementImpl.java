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
package net.sourceforge.htmlunit.html.dom;

import org.w3c.dom.html.HTMLBaseFontElement;

/**
 * @xerces.internal
 * @version $Revision$ $Date$
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLBaseFontElement
 * @see net.sourceforge.htmlunit.xerces.dom.ElementImpl
 */
public class HTMLBaseFontElementImpl
    extends HTMLElementImpl
    implements HTMLBaseFontElement
{
    
    private static final long serialVersionUID = -3650249921091097229L;

    public String getColor()
    {
        return capitalize( getAttribute( "color" ) );
    }
    
    
    public void setColor( String color )
    {
        setAttribute( "color", color );
    }
    
    
    public String getFace()
    {
        return capitalize( getAttribute( "face" ) );
    }
    
    
    public void setFace( String face )
    {
        setAttribute( "face", face );
    }
    
    
    public String getSize()
    {
        return getAttribute( "size" );
    }
    
    
    public void setSize( String size )
    {
        setAttribute( "size", size );
    }

    
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLBaseFontElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}

