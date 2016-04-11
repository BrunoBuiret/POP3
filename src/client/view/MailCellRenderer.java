package client.view;

import common.mail.Mail;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class MailCellRenderer extends JLabel implements ListCellRenderer<Mail>
{

    @Override
    public Component getListCellRendererComponent(JList<? extends Mail> list, Mail value, int index, boolean isSelected, boolean cellHasFocus)
    {
        // Fetch data
        String subject = value.getHeader("Subject");
        String from = value.getHeader("From");

        if(null == subject)
        {
            subject = "Sujet inconnu";
        }

        if(null != from)
        {
            from = from.replace("<", "&lt;");
            from = from.replace(">", "&gt;");
            from = from.replace("\"", "&quot;");
        }
        else
        {
            from = "Expéditeur inconnu";
        }

        // Build cell
        this.setText(String.format(
            "<html>"
            + "<div style='padding: 3px 2px; border-left: 3px solid %s;'>"
            + "%s<br/>"
            + "<span style='font-size: 90%%';>%s</span>"
            + "</div>"
            + "</html>",
            isSelected ? "blue" : "gray",
            subject,
            from
        ));

        return this;
    }
}
