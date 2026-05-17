/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import modelo.Producto;
import servicio.ProductoServicio;

public class FrameGestionProductos extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrameGestionProductos.class.getName());

    //servicios
    private ProductoServicio productoService;
    private Inicio inicio;

    private DefaultTableModel modeloTabla;

    /**
     * Creates new form FrameGestionProductos
     */
    public FrameGestionProductos() {
        initComponents();
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    //Constructor principal 
    public FrameGestionProductos(ProductoServicio productoService, Inicio inicio) {
        this();
        this.productoService = productoService;
        this.inicio = inicio;
        initCustomComponents();
        actualizarTabla();
    }

    // Configura los modelos de tabla, listeners y eventos de los botones.
    private void initCustomComponents() {

        modeloTabla = (DefaultTableModel) jTable1.getModel();

        // Establecemos que no se puedan editar las celdas.
        for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellEditor(null);
        }

        /* Listener de selección en la tabla: al seleccionar una fila se
         cargan los datos en los campos de texto para poder modificarlos o
        eliminarlos.*/
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSel = jTable1.getSelectedRow();
                if (filaSel >= 0) {
                    Object idVal = modeloTabla.getValueAt(filaSel, 0);
                    Object nombreVal = modeloTabla.getValueAt(filaSel, 1);
                    Object descVal = modeloTabla.getValueAt(filaSel, 2);
                    Object precioVal = modeloTabla.getValueAt(filaSel, 3);
                    jTextField1.setText(idVal != null ? idVal.toString() : "");
                    jTextField2.setText(nombreVal != null ? nombreVal.toString() : "");
                    jTextField3.setText(descVal != null ? descVal.toString() : "");
                    jTextField4.setText(precioVal != null ? precioVal.toString() : "");
                }
            }
        });

        // Configuramos las acciones de los botones según su funcionalidad
        btnAgregarProducto.addActionListener(e -> agregarProducto());
        btnModificarProducto.addActionListener(e -> modificarProducto());
        btnEliminarID.addActionListener(e -> eliminarProducto());
        btnLimpiarCampos.addActionListener(e -> limpiarCampos());
        btnConsultaProducto.addActionListener(e -> consultarProducto());
        btnVolverMenuPrincipal.addActionListener(e -> volverAlMenu());
    }

    /*
     Agrega un nuevo producto al catálogo validando los campos del
     formulario. Los datos se almacenan en memoria y también se guardan
     en el archivo XML.
     */
    private void agregarProducto() {
        try {
            String id = jTextField1.getText().trim();
            String nombre = jTextField2.getText().trim();
            String desc = jTextField3.getText().trim();
            double precio = Double.parseDouble(jTextField4.getText().trim());

            if (id.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "ID y nombre son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (productoService.existeId(id)) {
                JOptionPane.showMessageDialog(this,
                        "Ya existe un producto con el ID " + id + ".",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Producto p = new Producto(id, nombre, desc, precio);
            productoService.agregarProducto(p);
            JOptionPane.showMessageDialog(this, "Producto agregado correctamente.");
            limpiarCampos();
            actualizarTabla();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Precio inválido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
    Modifica los datos de un producto existente. Si no se encuentra por ID,
    informa al usuario.
     */
    private void modificarProducto() {
        try {
            String id = jTextField1.getText().trim();
            String nombre = jTextField2.getText().trim();
            String desc = jTextField3.getText().trim();
            double precio = Double.parseDouble(jTextField4.getText().trim());

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debe indicar el ID del producto a modificar (o seleccionar uno en la tabla).",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = productoService.actualizarProducto(id, nombre, desc, precio);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Producto modificado correctamente.");
                limpiarCampos();
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Precio inválido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Elimina un producto buscándolo por su ID.
    private void eliminarProducto() {
        String id = jTextField1.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Indique el ID del producto a eliminar (o selecciónelo en la tabla).",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = productoService.eliminarPorId(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Producto eliminado.");
            limpiarCampos();
            actualizarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.");
        }
    }

    // Actualiza la tabla de productos tomando la información del servicio.
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        Producto[] productos = productoService.obtenerTodos();
        if (productos == null) {
            return;
        }
        for (Producto p : productos) {
            if (p != null) {
                modeloTabla.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getDescripcion(),
                    p.getPrecio()
                });
            }
        }
    }

    //Consulta y carga un producto por ID o nombre.
    private void consultarProducto() {
        String idBuscado = jTextField5.getText().trim();
        String nombreBuscado = jTextField6.getText().trim();

        if (idBuscado.isEmpty() && nombreBuscado.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese ID o nombre para realizar la consulta.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Producto encontrado;

        if (!idBuscado.isEmpty()) {
            encontrado = productoService.buscarPorId(idBuscado);
        } else {
            encontrado = productoService.buscarPorNombre(nombreBuscado);
        }

        if (encontrado == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró ningún producto con los datos indicados.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jTextField1.setText(encontrado.getId());
        jTextField2.setText(encontrado.getNombre());
        jTextField3.setText(encontrado.getDescripcion());
        jTextField4.setText(String.valueOf(encontrado.getPrecio()));

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String idTabla = String.valueOf(modeloTabla.getValueAt(i, 0));
            if (idTabla.equals(encontrado.getId())) {
                jTable1.setRowSelectionInterval(i, i);
                jTable1.scrollRectToVisible(jTable1.getCellRect(i, 0, true));
                break;
            }
        }
    }

    //Limpia todos los campos del formulario y de los filtros.
    private void limpiarCampos() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTable1.clearSelection();
    }

    //Cierra esta ventana y devuelve el control a la ventana principal.
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
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        btnAgregarProducto = new javax.swing.JButton();
        btnEliminarID = new javax.swing.JButton();
        btnModificarProducto = new javax.swing.JButton();
        btnLimpiarCampos = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        btnVolverMenuPrincipal = new javax.swing.JButton();
        btnConsultaProducto = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        jPanel2.setBackground(new java.awt.Color(51, 0, 0));

        jLabel1.setFont(new java.awt.Font("Serif", 2, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Gestión de Productos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(347, 347, 347))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 153, 51));

        jLabel2.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 0, 0));
        jLabel2.setText("ID:");

        jLabel3.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 0, 0));
        jLabel3.setText("NOMBRE:");

        jLabel4.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 0, 0));
        jLabel4.setText("DESCRIPCIÓN:");

        jLabel5.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 0, 0));
        jLabel5.setText("PRECIO:");

        jTextField1.setBackground(new java.awt.Color(51, 0, 0));
        jTextField1.setForeground(new java.awt.Color(255, 255, 255));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setBackground(new java.awt.Color(51, 0, 0));
        jTextField2.setForeground(new java.awt.Color(255, 255, 255));

        jTextField3.setBackground(new java.awt.Color(51, 0, 0));
        jTextField3.setForeground(new java.awt.Color(255, 255, 255));

        jTextField4.setBackground(new java.awt.Color(51, 0, 0));
        jTextField4.setForeground(new java.awt.Color(255, 255, 255));

        btnAgregarProducto.setBackground(new java.awt.Color(51, 0, 0));
        btnAgregarProducto.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        btnAgregarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarProducto.setText("Agregar Producto");

        btnEliminarID.setBackground(new java.awt.Color(51, 0, 0));
        btnEliminarID.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        btnEliminarID.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarID.setText("Eliminar por ID");

        btnModificarProducto.setBackground(new java.awt.Color(51, 0, 0));
        btnModificarProducto.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        btnModificarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnModificarProducto.setText("Modificar Producto");

        btnLimpiarCampos.setBackground(new java.awt.Color(51, 0, 0));
        btnLimpiarCampos.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        btnLimpiarCampos.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiarCampos.setText("Limpiar Campos");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField1)
                    .addComponent(jTextField2)
                    .addComponent(jTextField3)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(63, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAgregarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .addComponent(btnEliminarID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnModificarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .addComponent(btnLimpiarCampos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(32, 32, 32))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarProducto)
                    .addComponent(btnModificarProducto))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminarID)
                    .addComponent(btnLimpiarCampos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 153, 51));

        jTable1.setBackground(new java.awt.Color(51, 0, 0));
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOMBRE", "DESCRIPCIÓN", "PRECIO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        jPanel5.setBackground(new java.awt.Color(255, 153, 51));

        jLabel7.setBackground(new java.awt.Color(51, 0, 0));
        jLabel7.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 0, 0));
        jLabel7.setText("Buscar por ID:");

        jLabel8.setBackground(new java.awt.Color(51, 0, 0));
        jLabel8.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 0, 0));
        jLabel8.setText("Buscar por Nombre: ");

        jTextField5.setBackground(new java.awt.Color(51, 0, 0));
        jTextField5.setForeground(new java.awt.Color(255, 255, 255));

        jTextField6.setBackground(new java.awt.Color(51, 0, 0));
        jTextField6.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField5)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(51, 0, 0));

        jLabel6.setFont(new java.awt.Font("Serif", 2, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Sección de Consulta");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        btnVolverMenuPrincipal.setBackground(new java.awt.Color(51, 0, 0));
        btnVolverMenuPrincipal.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        btnVolverMenuPrincipal.setForeground(new java.awt.Color(255, 255, 255));
        btnVolverMenuPrincipal.setText("Volver al Menú Principal");

        btnConsultaProducto.setBackground(new java.awt.Color(51, 0, 0));
        btnConsultaProducto.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        btnConsultaProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnConsultaProducto.setText("Consultar Producto");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(166, 166, 166)
                        .addComponent(btnConsultaProducto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnVolverMenuPrincipal)))
                .addGap(32, 32, 32))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVolverMenuPrincipal)
                    .addComponent(btnConsultaProducto))
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarProducto;
    private javax.swing.JButton btnConsultaProducto;
    private javax.swing.JButton btnEliminarID;
    private javax.swing.JButton btnLimpiarCampos;
    private javax.swing.JButton btnModificarProducto;
    private javax.swing.JButton btnVolverMenuPrincipal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
