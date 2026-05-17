package persistencia;

import arbol.ArbolBSTImpl;
import arbol.NodoArbol;
import modelo.Pedido;
import modelo.Producto;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
    Repositorio encargado de persistir y recuperar los pedidos almacenados en la aplicación mediante un archivo XML.
    La estructura se guarda recorriendo el árbol binario de búsqueda en orden, garantizando que los pedidos se escriban de forma ordenada
    por su identificador. Los productos de cada pedido también se serializan dentro de la etiqueta correspondiente. 
 */
public class PedidosXmlRepository {

    //Atributo
    private final String rutaArchivo;

    //Crea un repositorio usando la ruta del archivo XML indicado.
    public PedidosXmlRepository(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    /*
    Guarda todos los pedidos del árbol en un archivo XML.
    Se genera un documento DOM en memoria y se llena mediante un recorrido
    inorden del árbol, lo que asegura el ordenamiento por id.
     */
    public void guardar(ArbolBSTImpl arbol) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        //Elemento raíz <pedidos>
        Element root = doc.createElement("pedidos");
        doc.appendChild(root);

        //Se recorre el árbol si no está vacío
        if (arbol != null && arbol.getRaiz() != null) {
            guardarRec(arbol.getRaiz(), doc, root);
        }

        //Configurar transformador para escritura con identado
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        //Guardar archivo en disco
        DOMSource domSource = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(rutaArchivo));
        transformer.transform(domSource, result);
    }

    // Recorre el árbol en inorden y va agregando los pedidos al documento XML.
    private void guardarRec(NodoArbol nodo, Document doc, Element root) {
        if (nodo == null) {
            return;
        }
        // Lado izquierdo
        guardarRec(nodo.izquierdo, doc, root);

        // Serialización del pedido actual
        Pedido p = nodo.pedido;

        Element ePed = doc.createElement("pedido");
        root.appendChild(ePed);

        Element eId = doc.createElement("id");
        eId.setTextContent(p.getId());
        ePed.appendChild(eId);

        Element eCli = doc.createElement("cliente");
        eCli.setTextContent(p.getCliente());
        ePed.appendChild(eCli);

        Element eFecha = doc.createElement("fecha");
        eFecha.setTextContent(p.getFecha().toString());
        ePed.appendChild(eFecha);

        Element eEstado = doc.createElement("estado");
        eEstado.setTextContent(p.getEstado());
        ePed.appendChild(eEstado);

        // Serialización de productos asociados
        Producto[] productos = p.getProductos();
        if (productos != null && productos.length > 0) {
            Element eProductos = doc.createElement("productos");
            ePed.appendChild(eProductos);

            for (Producto prod : productos) {
                if (prod == null) {
                    continue;
                }
                Element eProd = doc.createElement("producto");

                Element ePid = doc.createElement("id");
                ePid.setTextContent(prod.getId());
                eProd.appendChild(ePid);

                Element eNom = doc.createElement("nombre");
                eNom.setTextContent(prod.getNombre());
                eProd.appendChild(eNom);

                Element eDesc = doc.createElement("descripcion");
                eDesc.setTextContent(prod.getDescripcion());
                eProd.appendChild(eDesc);

                Element ePrecio = doc.createElement("precio");
                ePrecio.setTextContent(Double.toString(prod.getPrecio()));
                eProd.appendChild(ePrecio);

                eProductos.appendChild(eProd);
            }
        }

        // Lado derecho
        guardarRec(nodo.derecho, doc, root);
    }

    //Carga el archivo XML y reconstruye el árbol de pedidos. 
    public ArbolBSTImpl cargar() throws Exception {
        ArbolBSTImpl arbol = new ArbolBSTImpl();
        File f = new File(rutaArchivo);

        // Si el archivo no existe, se retorna un árbol vacío
        if (!f.exists()) {
            return arbol;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(f);
        doc.getDocumentElement().normalize();

        NodeList lista = doc.getElementsByTagName("pedido");

        // Leer cada nodo <pedido>
        for (int i = 0; i < lista.getLength(); i++) {
            Node n = lista.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element ePed = (Element) n;

            String id = ePed.getElementsByTagName("id").item(0).getTextContent();
            String cliente = ePed.getElementsByTagName("cliente").item(0).getTextContent();
            LocalDate fecha = LocalDate.parse(
                    ePed.getElementsByTagName("fecha").item(0).getTextContent());
            String estado = ePed.getElementsByTagName("estado").item(0).getTextContent();

            Pedido p = new Pedido(id, cliente, fecha, estado);

            //Procesar productos del pedido
            NodeList listaProductosCont = ePed.getElementsByTagName("productos");
            if (listaProductosCont != null && listaProductosCont.getLength() > 0) {
                Element eProductos = (Element) listaProductosCont.item(0);
                NodeList productosNodes = eProductos.getElementsByTagName("producto");
                for (int j = 0; j < productosNodes.getLength(); j++) {
                    Node np = productosNodes.item(j);
                    if (np.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    Element eProd = (Element) np;

                    String pid = eProd.getElementsByTagName("id").item(0).getTextContent();
                    String nom = eProd.getElementsByTagName("nombre").item(0).getTextContent();
                    String desc = eProd.getElementsByTagName("descripcion").item(0).getTextContent();
                    double precio = Double.parseDouble(
                            eProd.getElementsByTagName("precio").item(0).getTextContent());

                    Producto prod = new Producto(pid, nom, desc, precio);
                    p.agregarProducto(prod);
                }
            }

            arbol.insertar(p);
        }

        return arbol;
    }
}
