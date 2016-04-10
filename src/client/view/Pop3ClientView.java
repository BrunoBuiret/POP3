package client.view;

import client.Pop3Client;
import common.mail.Mail;
import common.mail.MailBox;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class Pop3ClientView extends javax.swing.JFrame
{
    /**
     * Creates new form Pop3ClientView
     */
    public Pop3ClientView()
    {
        initComponents();
        this.executor = Executors.newFixedThreadPool(1);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        mailsList = new javax.swing.JList<>();
        this.mailsList.setCellRenderer(new MailCellRenderer());
        jScrollPane2 = new javax.swing.JScrollPane();
        mailView = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        addressLabel = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        portField = new javax.swing.JSpinner();
        userNameLabel = new javax.swing.JLabel();
        userNameField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        pathLabel = new javax.swing.JLabel();
        pathPanel = new javax.swing.JPanel();
        pathField = new javax.swing.JTextField();
        pathButton = new javax.swing.JButton();
        deleteAfterRetrievalField = new javax.swing.JCheckBox();
        useApopField = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        synchronizeButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client POP 3");
        setPreferredSize(new java.awt.Dimension(800, 500));
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[] {0};
        layout.rowHeights = new int[] {0, 5, 0, 5, 0};
        getContentPane().setLayout(layout);

        mailsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mailsList.setPreferredSize(new java.awt.Dimension(200, 80));
        mailsList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                mailsListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(mailsList);

        jSplitPane1.setLeftComponent(jScrollPane1);

        mailView.setEditable(false);
        mailView.setContentType("text/html"); // NOI18N
        jScrollPane2.setViewportView(mailView);

        jSplitPane1.setRightComponent(jScrollPane2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jSplitPane1, gridBagConstraints);

        java.awt.GridBagLayout jPanel2Layout = new java.awt.GridBagLayout();
        jPanel2Layout.columnWidths = new int[] {0, 5, 0};
        jPanel2Layout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        jPanel2.setLayout(jPanel2Layout);

        addressLabel.setText("Adresse du serveur");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel2.add(addressLabel, gridBagConstraints);

        addressField.setText("127.0.0.1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel2.add(addressField, gridBagConstraints);

        portLabel.setText("Port du serveur");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel2.add(portLabel, gridBagConstraints);

        portField.setModel(new javax.swing.SpinnerNumberModel(110, 1, 65535, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel2.add(portField, gridBagConstraints);

        userNameLabel.setText("Nom d'utilisateur");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel2.add(userNameLabel, gridBagConstraints);

        userNameField.setText("bruno.buiret");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel2.add(userNameField, gridBagConstraints);

        passwordLabel.setText("Mot de passe");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel2.add(passwordLabel, gridBagConstraints);

        passwordField.setText("bruno.buiret");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel2.add(passwordField, gridBagConstraints);

        pathLabel.setText("Chemin boîte mail local");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel2.add(pathLabel, gridBagConstraints);

        pathPanel.setLayout(new java.awt.GridBagLayout());

        pathField.setText("D:\\bruno.buiret.lmbox");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        pathPanel.add(pathField, gridBagConstraints);

        pathButton.setText("Parcourir");
        pathButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pathButtonActionPerformed(evt);
            }
        });
        pathPanel.add(pathButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel2.add(pathPanel, gridBagConstraints);

        deleteAfterRetrievalField.setText("Supprimer les messages après les avoir récupérés");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel2.add(deleteAfterRetrievalField, gridBagConstraints);

        useApopField.setText("Utiliser la commande APOP");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel2.add(useApopField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        synchronizeButton.setText("Synchronisation");
        synchronizeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                synchronizeButtonActionPerformed(evt);
            }
        });
        jPanel3.add(synchronizeButton);

        resetButton.setText("Réinitialiser");
        resetButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                resetButtonActionPerformed(evt);
            }
        });
        jPanel3.add(resetButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(jPanel3, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     *
     * @param evt
     */
    private void pathButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pathButtonActionPerformed
    {//GEN-HEADEREND:event_pathButtonActionPerformed
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogTitle("Emplacement du fichier local");
        fileDialog.setDialogType(JFileChooser.CUSTOM_DIALOG);
        fileDialog.setApproveButtonText("Sélectionner");

        if(fileDialog.showDialog(this, null) == JFileChooser.APPROVE_OPTION)
        {
            this.pathField.setText(fileDialog.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_pathButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetButtonActionPerformed
    {//GEN-HEADEREND:event_resetButtonActionPerformed
        this.addressField.setText(null);
        this.portField.setValue(110);
        this.userNameField.setText(null);
        this.passwordField.setText(null);
        this.pathField.setText(null);
    }//GEN-LAST:event_resetButtonActionPerformed

    private void synchronizeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_synchronizeButtonActionPerformed
    {//GEN-HEADEREND:event_synchronizeButtonActionPerformed
        this.executor.execute(()
            -> 
            {
                // Deactivate this button and the other fields
                this.synchronizeButton.setEnabled(false);
                this.resetButton.setEnabled(false);
                this.addressField.setEditable(false);
                this.portField.setEnabled(false);
                this.userNameField.setEditable(false);
                this.passwordField.setEditable(false);
                this.pathField.setEditable(false);
                this.pathButton.setEnabled(false);
                this.deleteAfterRetrievalField.setEnabled(false);
                this.useApopField.setEnabled(false);

                // Initialize vars
                InetAddress address = null;
                int port = (int) this.portField.getValue();
                String userName = this.userNameField.getText();
                String password = new String(this.passwordField.getPassword());
                String path = this.pathField.getText();
                boolean deleteAfterRetrieval = this.deleteAfterRetrievalField.isSelected();
                boolean useApop = this.useApopField.isSelected();

                try
                {
                    address = InetAddress.getByName(this.addressField.getText());
                }
                catch(UnknownHostException ex)
                {
                    address = null;
                }

                if(null != address && !userName.isEmpty() && !password.isEmpty() && !path.isEmpty())
                {
                    try
                    {
                        Pop3Client client = new Pop3Client(address, port, path, deleteAfterRetrieval);

                        // Try authenticating
                        boolean isAuthenticated = false;

                        if(useApop)
                        {
                            // Authenticate user using APOP
                            if(client.apop(userName) != 0)
                            {
                                // Authentication is successful
                                isAuthenticated = true;
                            }
                            else
                            {
                                // Authentication failed completly
                                JOptionPane.showMessageDialog(
                                    this,
                                    "Votre identifiant est incorrect.",
                                    "Authentification incorrecte",
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                        else // Authenticate user using USER / PASS
                        {
                            if(client.user(userName) != 0)
                            {
                                // Authentication is being successful so far
                                // Continue authenticating
                                if(client.pass(password) != 0)
                                {
                                    // Authentication is successful
                                    isAuthenticated = true;
                                }
                                else
                                {
                                    // Authentication failed completly
                                    JOptionPane.showMessageDialog(
                                        this,
                                        "Votre mot de passe est incorrect.",
                                        "Authentification incorrecte",
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            }
                            else
                            {
                                // Authentication has already failed
                                JOptionPane.showMessageDialog(
                                    this,
                                    "Votre identifiant est incorrect.",
                                    "Authentification incorrecte",
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }

                        if(isAuthenticated)
                        {
                            // Try getting the number of emails
                            if(client.list() != 0)
                            {
                                // The number of emails has been fetched
                                int failedRetrievalsNumber = 0;
                                int failedDeletionsNumber = 0;

                                for(int i = 0; i < client.getMessagesNumber(); i++)
                                {
                                    if(client.retrieve(i + 1) != 0)
                                    {
                                        if(deleteAfterRetrieval && client.delete(i + 1) == 0)
                                        {
                                            failedDeletionsNumber++;
                                        }
                                    }
                                    else
                                    {
                                        failedRetrievalsNumber++;
                                    }
                                }

                                // Was there failures retrieving and deleting emails?
                                if(failedRetrievalsNumber > 0 && failedDeletionsNumber == 0)
                                {
                                    JOptionPane.showMessageDialog(
                                        this,
                                        "La récupération de certains des emails a échoué.",
                                        "Echec de la récupération",
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                }
                                else if(failedRetrievalsNumber == 0 && failedDeletionsNumber > 0)
                                {
                                    JOptionPane.showMessageDialog(
                                        this,
                                        "La suppression de certains des emails a échoué.",
                                        "Echec de la suppression",
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                }
                                else if(failedRetrievalsNumber > 0 && failedDeletionsNumber > 0)
                                {
                                    JOptionPane.showMessageDialog(
                                        this,
                                        "La récupération de certains des emails a échoué.\nDe même, certaines suppressions ont aussi échoué.",
                                        "Echec de la suppression",
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                }

                                this.refreshMailsList();

                                // No more business, close the connection
                                if(client.quit() == 0)
                                {
                                    JOptionPane.showMessageDialog(
                                        this,
                                        "Une erreur est est survenue lors de la mise à jour sur le serveur.",
                                        "Echec lors de la mise à jour",
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            }
                            else
                            {
                                // The number of emails couldn't be fetched.
                                JOptionPane.showMessageDialog(
                                    this,
                                    "Votre mot de passe est incorrect.",
                                    "Authentification incorrecte",
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        throw e;
                        /*
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Une erreur est survenue lors de la synchronisation.\n\n" + e.getMessage(),
                                    "Erreur",
                                    JOptionPane.ERROR_MESSAGE
                            );
                         */
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(
                        this,
                        "Les paramètres que vous avez rentrés sont incorrects.",
                        "Paramètres incorrects",
                        JOptionPane.ERROR_MESSAGE
                    );
                }

                // Activate this button and the other fields
                this.synchronizeButton.setEnabled(true);
                this.resetButton.setEnabled(true);
                this.addressField.setEditable(true);
                this.portField.setEnabled(true);
                this.userNameField.setEditable(true);
                this.passwordField.setEditable(true);
                this.pathField.setEditable(true);
                this.pathButton.setEnabled(true);
                this.deleteAfterRetrievalField.setEnabled(true);
                this.useApopField.setEnabled(true);
        });
    }//GEN-LAST:event_synchronizeButtonActionPerformed

    private void mailsListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_mailsListValueChanged
    {//GEN-HEADEREND:event_mailsListValueChanged
        Mail currentMail = this.mailsList.getSelectedValue();

        if(null != currentMail)
        {
            // Build view
            StringBuilder viewBuilder = new StringBuilder();
            viewBuilder.append("<html><head><meta charset='iso-8859-1'/></head><body>");

            if(currentMail.getHeaders().size() > 0)
            {
                viewBuilder.append("<p>");

                for(Map.Entry<String, String> entry : currentMail.getHeaders().entrySet())
                {
                    viewBuilder.append(entry.getKey());
                    viewBuilder.append(": ");
                    viewBuilder.append(entry.getValue());
                    viewBuilder.append("<br/>");
                }

                viewBuilder.append("</p>");
            }

            viewBuilder.append("<p>");
            viewBuilder.append(currentMail.getContents());
            viewBuilder.append("</p></body></html>");

            this.mailView.setText(viewBuilder.toString());
        }
        else
        {
            this.mailView.setText(null);
        }
    }//GEN-LAST:event_mailsListValueChanged

    /**
     *
     */
    private void refreshMailsList()
    {
        String pathValue = this.pathField.getText();

        if(!pathValue.isEmpty())
        {
            File path = new File(pathValue);

            if(path.exists() && path.isFile() && path.canRead())
            {
                DefaultListModel<Mail> newModel = new DefaultListModel<>();

                try
                {
                    // Initialize vars
                    MailBox mailBox = new MailBox(pathValue);

                    // Read mailbox
                    mailBox.read();

                    for(int i = 0, j = mailBox.getSize(); i < j; i++)
                    {
                        newModel.addElement(mailBox.get(i));
                    }

                    this.mailsList.setModel(newModel);
                }
                catch(FileNotFoundException | IllegalArgumentException ex)
                {
                    Logger.getLogger(Pop3ClientView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        try
        {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(Pop3ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(()
            -> 
            {
                new Pop3ClientView().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JCheckBox deleteAfterRetrievalField;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextPane mailView;
    private javax.swing.JList<Mail> mailsList;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JButton pathButton;
    private javax.swing.JTextField pathField;
    private javax.swing.JLabel pathLabel;
    private javax.swing.JPanel pathPanel;
    private javax.swing.JSpinner portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton synchronizeButton;
    private javax.swing.JCheckBox useApopField;
    private javax.swing.JTextField userNameField;
    private javax.swing.JLabel userNameLabel;
    // End of variables declaration//GEN-END:variables
    private final ExecutorService executor;
}
