/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
import javax.swing.DefaultListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Pedido;
import modelo.Producto;
import servicio.PedidoServicio;
import servicio.ProductoServicio;

public class FrameGestionPedidos extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrameGestionPedidos.class.getName());

    //Servicios
    private PedidoServicio pedidoService;
    private ProductoServicio productoService;
    private Inicio inicio;

    private DefaultTableModel modeloTablaPedidos;
    private DefaultTableModel modeloTablaProductos;
    private DefaultListModel<Producto> modeloListaProductos;
    private JList<Producto> listaProductos;

    /**
     * Creates new form FrameGestionPedidos
     */
    public FrameGestionPedidos() {
        initComponents();
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    //Constructor principal 
    public FrameGestionPedidos(PedidoServicio pedidoService, ProductoServicio productoService, Inicio inicio) {
        this();
        this.pedidoService = pedidoService;
        this.productoService = productoService;
        this.inicio = inicio;
        initCustomComponents();
        actualizarTablaGeneral();
        cargarProductosEnLista();
    }

    //Configura modelos, combos, listas y listeners de las tablas y botones.
    private void initCustomComponents() {
        // Configuramos el combo de estado con las mismas opciones que en la vista original
        jComboBox1.setModel(new DefaultComboBoxModel<>(new String[]{
            "Todos", "PENDIENTE", "EN_PREPARACION", "ENVIADO", "COMPLETADO"
        }));

        // Obtenemos los modelos de las tablas existentes
        modeloTablaPedidos = (DefaultTableModel) jTable2.getModel();
        modeloTablaProductos = (DefaultTableModel) jTable1.getModel();

        // Desactivamos la edición de celdas
        for (int i = 0; i < modeloTablaPedidos.getColumnCount(); i++) {
            jTable2.getColumnModel().getColumn(i).setCellEditor(null);
        }
        for (int i = 0; i < modeloTablaProductos.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellEditor(null);
        }

        // Creamos la lista de productos disponibles y la añadimos al scroll correspondiente
        modeloListaProductos = new DefaultListModel<>();
        listaProductos = new JList<>(modeloListaProductos);
        listaProductos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(listaProductos);

        /* Listener de selección en la tabla de pedidos: al seleccionar un pedido se
        cargan sus datos en los campos de texto y se muestran sus productos.*/
        jTable2.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = jTable2.getSelectedRow();
                if (fila >= 0) {
                    Object idObj = modeloTablaPedidos.getValueAt(fila, 0);
                    Object cliObj = modeloTablaPedidos.getValueAt(fila, 1);
                    String idPedido = idObj != null ? idObj.toString() : "";
                    String cliente = cliObj != null ? cliObj.toString() : "";
                    jTextField1.setText(idPedido);
                    jTextField2.setText(cliente);
                    actualizarTablaProductosDePedido(idPedido);
                }
            }
        });

        // Asignamos las acciones a cada botón
        btnCrearPedido.addActionListener(e -> crearPedido());
        btnAgregarProducto.addActionListener(e -> agregarProductosSeleccionados());
        btnDeshacerUltimaAccion.addActionListener(e -> deshacer());
        btnMarcarCompletado.addActionListener(e -> marcarComoCompletado());
        btnEliminarPedidoCompletado.addActionListener(e -> eliminarPedido());
        btnLimpiarCampos.addActionListener(e -> limpiarCampos());
        btnBuscarCliente.addActionListener(e -> filtrarPorCliente());
        btnBuscarEstado.addActionListener(e -> filtrarPorEstado());
        btnVolverMenuPrincipal.addActionListener(e -> volverAlMenu());
    }

    //Crea un nuevo pedido validando todos los campos.
    private void crearPedido() {
        String id = jTextField1.getText().trim();
        String cli = jTextField2.getText().trim();

        if (id.isEmpty() || cli.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "ID de pedido y cliente son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pedidoService.existeId(id)) {
            JOptionPane.showMessageDialog(this,
                    "Ya existe un pedido con el ID " + id + ".",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        pedidoService.crearPedido(id, cli);
        JOptionPane.showMessageDialog(this,
                "Pedido creado correctamente.\n"
                + "Se ha generado automáticamente la tarea de preparación.\n"
                + "El estado inicial se ha asignado según la cola de tareas.");
        limpiarCampos();
        actualizarTablaGeneral();
    }

    //Elimina un pedido, solo si está COMPLETADO.
    private void eliminarPedido() {
        String id = jTextField1.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Indique el ID del pedido a eliminar.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pedido p = pedidoService.buscarPorId(id);
        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "No existe un pedido con ID " + id + ".",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String estado = p.getEstado() == null ? "" : p.getEstado().toUpperCase();
        if (!"COMPLETADO".equals(estado)) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden eliminar pedidos con estado COMPLETADO.",
                    "Restricción", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int r = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar el pedido " + id + "?",
                "Confirmación", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = pedidoService.eliminarPedido(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Pedido eliminado correctamente.");
            limpiarCampos();
            actualizarTablaGeneral();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el pedido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Agrega múltiples productos seleccionados al pedido indicado.
    private void agregarProductosSeleccionados() {
        String idPed = jTextField1.getText().trim();

        if (idPed.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Indique el ID de pedido antes de agregar productos.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int[] indices = listaProductos.getSelectedIndices();
        if (indices == null || indices.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione uno o más productos en la lista.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int indice : indices) {
            Producto p = modeloListaProductos.getElementAt(indice);
            pedidoService.agregarProductoAPedido(idPed, p);
        }

        JOptionPane.showMessageDialog(this,
                "Producto(s) agregado(s) al pedido.");
        actualizarTablaProductosDePedido(idPed);
        listaProductos.clearSelection();
    }

    //Marca un pedido como COMPLETADO si ya está ENVIADO.
    private void marcarComoCompletado() {
        String idPed = jTextField1.getText().trim();
        if (idPed.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Indique ID de pedido.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pedido pedido = pedidoService.buscarPorId(idPed);
        if (pedido == null) {
            JOptionPane.showMessageDialog(this,
                    "Pedido no encontrado.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!"ENVIADO".equalsIgnoreCase(pedido.getEstado())) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden marcar como COMPLETADO los pedidos en estado ENVIADO.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        pedidoService.cambiarEstado(idPed, "COMPLETADO");
        JOptionPane.showMessageDialog(this, "Pedido marcado como COMPLETADO.");
        actualizarTablaGeneral();
    }

    //Deshace la última operación realizada.
    private void deshacer() {
        String msg = pedidoService.deshacerUltimaAccion();
        JOptionPane.showMessageDialog(this, msg);
        actualizarTablaGeneral();
    }

    //Actualiza la tabla de pedidos completa.
    private void actualizarTablaGeneral() {
        llenarTablaDesdeListado(pedidoService.listarPedidosOrdenados());
        modeloTablaProductos.setRowCount(0);
    }

    //Filtra los pedidos por nombre de cliente.
    private void filtrarPorCliente() {
        String cli = jTextField3.getText().trim();
        if (cli.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el nombre del cliente para filtrar.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        llenarTablaDesdeListado(pedidoService.listarPedidosPorCliente(cli));
        modeloTablaProductos.setRowCount(0);
    }

    //Filtra los pedidos por estado seleccionado.
    private void filtrarPorEstado() {
        String estado = (String) jComboBox1.getSelectedItem();
        if ("Todos".equals(estado)) {
            actualizarTablaGeneral();
        } else {
            llenarTablaDesdeListado(pedidoService.listarPedidosPorEstado(estado));
            modeloTablaProductos.setRowCount(0);
        }
    }

    //Convierte el texto devuelto por el árbol en filas de la tabla de pedidos.
    private void llenarTablaDesdeListado(String listado) {
        modeloTablaPedidos.setRowCount(0);
        if (listado == null || listado.isBlank()) {
            return;
        }
        String[] lineas = listado.split("\\n");
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) {
                continue;
            }

            String[] partes = linea.split(" - ");
            if (partes.length >= 4) {
                String id = partes[0].replace("Pedido ", "").trim();
                String cliente = partes[1].trim();
                String fecha = partes[2].trim();
                String estado = partes[3].trim();
                modeloTablaPedidos.addRow(new Object[]{id, cliente, fecha, estado});
            } else {
                modeloTablaPedidos.addRow(new Object[]{linea, "", "", ""});
            }
        }
    }

    //Actualiza la tabla de productos del pedido seleccionado.
    private void actualizarTablaProductosDePedido(String idPedido) {
        modeloTablaProductos.setRowCount(0);
        if (idPedido == null || idPedido.isBlank()) {
            return;
        }
        Pedido pedido = pedidoService.buscarPorId(idPedido);
        if (pedido == null) {
            return;
        }
        Producto[] productos = pedido.getProductos();
        if (productos == null) {
            return;
        }
        for (Producto p : productos) {
            if (p != null) {
                modeloTablaProductos.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio()});
            }
        }
    }

    //Carga los productos del catálogo en la lista lateral.
    private void cargarProductosEnLista() {
        modeloListaProductos.clear();
        Producto[] productos = productoService.obtenerTodos();
        if (productos != null) {
            for (Producto p : productos) {
                if (p != null) {
                    modeloListaProductos.addElement(p);
                }
            }
        }
    }

    //Limpia todos los campos de formulario y deselecciona.
    private void limpiarCampos() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        if (jComboBox1.getItemCount() > 0) {
            jComboBox1.setSelectedIndex(0);
        }
        jTable2.clearSelection();
        listaProductos.clearSelection();
        modeloTablaProductos.setRowCount(0);
    }

    //cierra esta ventana y regresa al menú principal.
    private void volverAlMenu() {
        this.dispose();
        inicio.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        btnCrearPedido = new javax.swing.JButton();
        btnAgregarProducto = new javax.swing.JButton();
        btnDeshacerUltimaAccion = new javax.swing.JButton();
        btnMarcarCompletado = new javax.swing.JButton();
        btnEliminarPedidoCompletado = new javax.swing.JButton();
        btnLimpiarCampos = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        btnBuscarCliente = new javax.swing.JButton();
        btnBuscarEstado = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnVolverMenuPrincipal = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        jPanel2.setBackground(new java.awt.Color(51, 0, 0));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Serif", 2, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Gestión de Pedidos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(552, 552, 552)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 153, 51));

        jLabel2.setBackground(new java.awt.Color(51, 0, 0));
        jLabel2.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 0, 0));
        jLabel2.setText("ID Pedido:");

        jLabel3.setBackground(new java.awt.Color(51, 0, 0));
        jLabel3.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 0, 0));
        jLabel3.setText("Cliente:");

        jTextField1.setBackground(new java.awt.Color(51, 0, 0));
        jTextField1.setForeground(new java.awt.Color(255, 255, 255));

        jTextField2.setBackground(new java.awt.Color(51, 0, 0));
        jTextField2.setForeground(new java.awt.Color(255, 255, 255));

        btnCrearPedido.setBackground(new java.awt.Color(51, 0, 0));
        btnCrearPedido.setFont(new java.awt.Font("Serif", 0, 16)); // NOI18N
        btnCrearPedido.setForeground(new java.awt.Color(255, 255, 255));
        btnCrearPedido.setText("Crear Pedido");

        btnAgregarProducto.setBackground(new java.awt.Color(51, 0, 0));
        btnAgregarProducto.setFont(new java.awt.Font("Serif", 0, 16)); // NOI18N
        btnAgregarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarProducto.setText("Agregar Producto");

        btnDeshacerUltimaAccion.setBackground(new java.awt.Color(51, 0, 0));
        btnDeshacerUltimaAccion.setFont(new java.awt.Font("Serif", 0, 16)); // NOI18N
        btnDeshacerUltimaAccion.setForeground(new java.awt.Color(255, 255, 255));
        btnDeshacerUltimaAccion.setText("Deshacer Última Acción");

        btnMarcarCompletado.setBackground(new java.awt.Color(51, 0, 0));
        btnMarcarCompletado.setFont(new java.awt.Font("Serif", 0, 16)); // NOI18N
        btnMarcarCompletado.setForeground(new java.awt.Color(255, 255, 255));
        btnMarcarCompletado.setText("Marcar como Completado");

        btnEliminarPedidoCompletado.setBackground(new java.awt.Color(51, 0, 0));
        btnEliminarPedidoCompletado.setFont(new java.awt.Font("Serif", 0, 16)); // NOI18N
        btnEliminarPedidoCompletado.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarPedidoCompletado.setText("Eliminar pedido Completado");

        btnLimpiarCampos.setBackground(new java.awt.Color(51, 0, 0));
        btnLimpiarCampos.setFont(new java.awt.Font("Serif", 0, 16)); // NOI18N
        btnLimpiarCampos.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiarCampos.setText("Limpiar Campos");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnCrearPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDeshacerUltimaAccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEliminarPedidoCompletado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnMarcarCompletado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnLimpiarCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(77, 77, 77)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(51, 51, 51))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCrearPedido)
                    .addComponent(btnMarcarCompletado))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarProducto)
                    .addComponent(btnEliminarPedidoCompletado))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeshacerUltimaAccion)
                    .addComponent(btnLimpiarCampos))
                .addGap(28, 28, 28))
        );

        jPanel4.setBackground(new java.awt.Color(255, 153, 51));

        jScrollPane1.setBackground(new java.awt.Color(51, 0, 0));
        jScrollPane1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 0, 0));
        jLabel4.setText("Productos Disponibles");

        jLabel5.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 0, 0));
        jLabel5.setText("Productos del Pedido Seleccionado");

        jTable1.setBackground(new java.awt.Color(51, 0, 0));
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID PRODUCTO", "NOMBRE", "PRECIO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(93, 93, 93)))
                .addGap(27, 27, 27))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(51, 0, 0));
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Serif", 2, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Sección de Consultas");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 153, 51));

        jLabel8.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 0, 0));
        jLabel8.setText("Cliente:");

        jLabel9.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 0, 0));
        jLabel9.setText("Estado:");

        jTextField3.setBackground(new java.awt.Color(51, 0, 0));
        jTextField3.setForeground(new java.awt.Color(255, 255, 255));
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jComboBox1.setBackground(new java.awt.Color(51, 0, 0));
        jComboBox1.setForeground(new java.awt.Color(255, 255, 255));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        btnBuscarCliente.setBackground(new java.awt.Color(51, 0, 0));
        btnBuscarCliente.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        btnBuscarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscarCliente.setText("Buscar por Cliente");

        btnBuscarEstado.setBackground(new java.awt.Color(51, 0, 0));
        btnBuscarEstado.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        btnBuscarEstado.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscarEstado.setText("Buscar por Estado");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(89, 89, 89))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                    .addComponent(jTextField3))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBuscarEstado, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(jComboBox1))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnBuscarEstado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(51, 0, 0));

        jLabel6.setFont(new java.awt.Font("Serif", 2, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Información de los Pedidos");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(262, 262, 262)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
        );

        jPanel8.setBackground(new java.awt.Color(255, 153, 51));
        jPanel8.setForeground(new java.awt.Color(255, 255, 255));

        jTable2.setBackground(new java.awt.Color(51, 0, 0));
        jTable2.setForeground(new java.awt.Color(255, 255, 255));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "CLIENTE", "FECHA", "ESTADO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 765, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        btnVolverMenuPrincipal.setBackground(new java.awt.Color(51, 0, 0));
        btnVolverMenuPrincipal.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        btnVolverMenuPrincipal.setForeground(new java.awt.Color(255, 255, 255));
        btnVolverMenuPrincipal.setText("Volver al Menú Principal");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(btnVolverMenuPrincipal)))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(btnVolverMenuPrincipal)
                        .addGap(28, 28, 28)))
                .addGap(0, 20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarProducto;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarEstado;
    private javax.swing.JButton btnCrearPedido;
    private javax.swing.JButton btnDeshacerUltimaAccion;
    private javax.swing.JButton btnEliminarPedidoCompletado;
    private javax.swing.JButton btnLimpiarCampos;
    private javax.swing.JButton btnMarcarCompletado;
    private javax.swing.JButton btnVolverMenuPrincipal;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
