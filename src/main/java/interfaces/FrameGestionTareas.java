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
import modelo.Tarea;
import servicio.TareaServicio;
import servicio.PedidoServicio;

public class FrameGestionTareas extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrameGestionTareas.class.getName());

    // Servicios y referencia al menú principal
    private TareaServicio tareaService;
    private PedidoServicio pedidoService;
    private Inicio inicio;
    private DefaultTableModel modeloTabla;

    /**
     * Creates new form FrameGestionTareas
     */
    public FrameGestionTareas() {
        initComponents();
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    //Constructor principal 
    public FrameGestionTareas(TareaServicio tareaService, PedidoServicio pedidoService, Inicio inicio) {
        this();
        this.tareaService = tareaService;
        this.pedidoService = pedidoService;
        this.inicio = inicio;
        initCustomComponents();
        actualizarTablaTareas();
    }

    //Configura el modelo de la tabla y los listeners de los botones.
    private void initCustomComponents() {
        modeloTabla = (DefaultTableModel) jTable1.getModel();

        for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellEditor(null);
        }
        // Asignar acciones a botones
        btnGenerarTarea.addActionListener(e -> generar());
        btnEmpezarEnvios.addActionListener(e -> procesar());
        btnTareaActual.addActionListener(e -> verActual());
        btnLimpiarCampos.addActionListener(e -> limpiarCampos());
        btnVolverMenuPrincipal.addActionListener(e -> volverAlMenu());
    }

    //Actualiza la tabla con las tareas que se encuentran en la cola.
    private void actualizarTablaTareas() {
        modeloTabla.setRowCount(0);
        Tarea[] tareas = tareaService.obtenerTareasEnCola();
        if (tareas == null) {
            return;
        }
        for (Tarea t : tareas) {
            modeloTabla.addRow(new Object[]{
                t.getIdPedido(),
                t.getDescripcion()
            });
        }
    }

    //Genera una nueva tarea manual y la encola mediante TareaServicio.
    private void generar() {
        String id = jTextField1.getText().trim();
        String desc = jTextField2.getText().trim();

        if (id.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "ID de pedido y descripción son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Tarea[] tareasEnCola = tareaService.obtenerTareasEnCola();
        if (tareasEnCola != null) {
            for (Tarea t : tareasEnCola) {
                if (t != null && id.equals(t.getIdPedido())) {
                    JOptionPane.showMessageDialog(this,
                            "Ya existe una tarea en la cola para el pedido " + id + ".",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }

        tareaService.generarTarea(id, desc);
        JOptionPane.showMessageDialog(this, "Tarea generada y encolada correctamente.");

        jTextField2.setText("");
        actualizarTablaTareas();
    }

    //Muestra la tarea actual la más antigua en la cola.
    private void verActual() {
        Tarea t = tareaService.obtenerTareaActual();
        if (t == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay tareas en preparación.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tarea actual (la más antigua):\n" + t,
                    "Tarea en preparación", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /* Procesa la siguiente tarea de la cola. Cuando una tarea se procesa, se
      marca automáticamente el pedido como ENVIADO.*/
    private void procesar() {
        if (!tareaService.hayTareas()) {
            JOptionPane.showMessageDialog(this,
                    "No hay tareas pendientes de envío.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Tarea t = tareaService.procesarSiguienteTarea();
        if (t != null) {
            // El pedido pasa automáticamente a estado ENVIADO
            pedidoService.cambiarEstado(t.getIdPedido(), "ENVIADO");
            JOptionPane.showMessageDialog(this,
                    "Procesando envío del pedido " + t.getIdPedido()
                    + ". El pedido ha sido marcado como ENVIADO.",
                    "Envío procesado", JOptionPane.INFORMATION_MESSAGE);
        }
        actualizarTablaTareas();
    }

    //Limpia los campos de entrada.
    private void limpiarCampos() {
        jTextField1.setText("");
        jTextField2.setText("");
    }

    //Cierra esta ventana y retorna a la ventana principal.
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
        btnGenerarTarea = new javax.swing.JButton();
        btnEmpezarEnvios = new javax.swing.JButton();
        btnTareaActual = new javax.swing.JButton();
        btnLimpiarCampos = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnVolverMenuPrincipal = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        jPanel2.setBackground(new java.awt.Color(51, 0, 0));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Serif", 2, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Gestión de Tareas");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(429, 429, 429))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 153, 51));

        jLabel2.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 0, 0));
        jLabel2.setText("ID PEDIDO: ");

        jLabel3.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 0, 0));
        jLabel3.setText("Descripción de la Tarea: ");

        jTextField1.setBackground(new java.awt.Color(51, 0, 0));
        jTextField1.setForeground(new java.awt.Color(255, 255, 255));

        jTextField2.setBackground(new java.awt.Color(51, 0, 0));
        jTextField2.setForeground(new java.awt.Color(255, 255, 255));

        btnGenerarTarea.setBackground(new java.awt.Color(51, 0, 0));
        btnGenerarTarea.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        btnGenerarTarea.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerarTarea.setText("Generar tarea ");

        btnEmpezarEnvios.setBackground(new java.awt.Color(51, 0, 0));
        btnEmpezarEnvios.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        btnEmpezarEnvios.setForeground(new java.awt.Color(255, 255, 255));
        btnEmpezarEnvios.setText("Empezar Envíos");

        btnTareaActual.setBackground(new java.awt.Color(51, 0, 0));
        btnTareaActual.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        btnTareaActual.setForeground(new java.awt.Color(255, 255, 255));
        btnTareaActual.setText("Ver Tarea Actual");

        btnLimpiarCampos.setBackground(new java.awt.Color(51, 0, 0));
        btnLimpiarCampos.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        btnLimpiarCampos.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiarCampos.setText("Limpiar Campos");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnEmpezarEnvios, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGenerarTarea, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLimpiarCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTareaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGenerarTarea)
                    .addComponent(btnTareaActual))
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEmpezarEnvios)
                    .addComponent(btnLimpiarCampos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 153, 51));

        jTable1.setBackground(new java.awt.Color(51, 0, 0));
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "ID PEDIDO ", "DESCRIPCIÓN-ESTADO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
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
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        jPanel5.setBackground(new java.awt.Color(51, 0, 0));
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Serif", 2, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("OPERACIONES");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(158, 158, 158)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel4)
                .addContainerGap(22, Short.MAX_VALUE))
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
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnVolverMenuPrincipal)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(62, 62, 62)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(btnVolverMenuPrincipal)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmpezarEnvios;
    private javax.swing.JButton btnGenerarTarea;
    private javax.swing.JButton btnLimpiarCampos;
    private javax.swing.JButton btnTareaActual;
    private javax.swing.JButton btnVolverMenuPrincipal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
