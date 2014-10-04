package com.timepath;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    @Nullable
    public static String get(@NotNull Node root, @NonNls @NotNull String key) {
        try {
            @NotNull List<Node> elements = getElements(root, key);
            if (elements.isEmpty()) return null;
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
    @NotNull
    public static List<Node> getElements(@NotNull Node root, @NonNls @NotNull String expression) {
        @NotNull String[] path = expression.split("/");
        @NotNull List<Node> nodes = new LinkedList<>();
        nodes.add(root);
        for (String part : path) {
            @NotNull List<Node> temp = new LinkedList<>();
            for (@NotNull Node scan : nodes) {
                for (@NotNull Node node : get(scan, Node.ELEMENT_NODE)) {
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
    @NotNull
    public static List<Node> get(@NotNull Node parent, short nodeType) {
        @NotNull List<Node> list = new LinkedList<>();
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
    @Nullable
    public static <E> E last(@Nullable List<E> list) {
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
    public static Node rootNode(@NotNull InputStream is, @NonNls @NotNull String name)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(is);
        return getElements(doc, name).get(0);
    }

    public static String pprint(Node n) {
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            @NotNull DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();
            writer.getDomConfig().setParameter("format-pretty-print", true);
            writer.getDomConfig().setParameter("xml-declaration", false);
            return writer.writeToString(n);
        } catch (@NotNull ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOG.log(Level.SEVERE, null, e);
            return String.valueOf(n);
        }
    }

    public static String pprint(Source xmlInput, int indent) {
        try {
            @NotNull StringWriter stringWriter = new StringWriter();
            @NotNull StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (@NotNull IllegalArgumentException | TransformerException e) {
            LOG.log(Level.SEVERE, null, e);
            return String.valueOf(xmlInput);
        }
    }
}
