package com.timepath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class XMLUtils {

    private static final Logger LOG = Logger.getLogger(XMLUtils.class.getName());

    private XMLUtils() {
    }

    /**
     * Attempts to get the last text node by key
     *
     * @param root
     * @param key
     * @return the text, or null
     */
    public static String get(Node root, String key) {
        try {
            List<Node> elements = getElements(root, key);
            if (elements.size() == 0) return null;
            Node firstChild = last(elements).getFirstChild();
            if (firstChild == null) return null;
            return firstChild.getNodeValue();
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    /**
     * Get a list of nodes from the '/' delimited expression
     *
     * @param root
     * @param expression
     * @return
     */
    public static List<Node> getElements(Node root, String expression) {
        String[] path = expression.split("/");
        List<Node> nodes = new LinkedList<>();
        nodes.add(root);
        for (String part : path) {
            List<Node> temp = new LinkedList<>();
            for (Node scan : nodes) {
                for (Node node : get(scan, Node.ELEMENT_NODE)) {
                    if (node.getNodeName().equals(part)) {
                        temp.add(node);
                    }
                }
            }
            nodes = temp;
        }
        return nodes;
    }

    /**
     * Get direct descendants by type
     *
     * @param parent
     * @param nodeType
     * @return
     */
    public static List<Node> get(Node parent, short nodeType) {
        List<Node> list = new LinkedList<>();
        if (parent.hasChildNodes()) {
            NodeList nodes = parent.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                try {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == nodeType) {
                        list.add(node);
                    }
                } catch (NullPointerException ignored) {
                    // nodes.item() is broken
                    break;
                }
            }
        }
        return list;
    }

    /**
     * Get the last item in a list, or null
     */
    public static <E> E last(List<E> list) {
        return ((list == null) || list.isEmpty()) ? null : list.get(list.size() - 1);
    }

    /**
     * Fetch the first root by name from the given stream
     *
     * @param is
     * @param name
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Node rootNode(final InputStream is, String name)
            throws ParserConfigurationException, IOException, SAXException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(is);
        return getElements(doc, name).get(0);
    }

    public static String pprint(Node n) {
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();
            writer.getDomConfig().setParameter("format-pretty-print", true);
            writer.getDomConfig().setParameter("xml-declaration", false);
            return writer.writeToString(n);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOG.log(Level.SEVERE, null, e);
            return String.valueOf(n);
        }
    }

    public static String pprint(Source xmlInput, int indent) {
        try {
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (IllegalArgumentException | TransformerException e) {
            LOG.log(Level.SEVERE, null, e);
            return String.valueOf(xmlInput);
        }
    }
}
