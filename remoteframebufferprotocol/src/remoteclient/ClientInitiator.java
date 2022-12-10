package remoteclient;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import java.net.*;

public class ClientInitiator {

    public static void main(String args[]) throws Exception {

       
        
        final JLabel label = new JLabel("", SwingConstants.CENTER);

        final JFrame frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(label, BorderLayout.CENTER);
        final Dimension preferredSize = new Dimension(1000, 1000);
      frame.setPreferredSize(preferredSize);
        
        frame.setVisible(true);
        frame.pack();
      
        final ImageUpdateWorker task = new ImageUpdateWorker(label);
        task.execute();
    }

    public static class ImageUpdateWorker extends SwingWorker<Void, Icon> {
        
        private JLabel label;
       // final List<IconInfo> iconInfoList;

        ImageUpdateWorker(JLabel label) {
            this.label = label;
            
        }

        @Override
        public Void doInBackground() throws UnknownHostException, IOException {
        
        
        Socket socket=new Socket("localhost",5900);
        int wid,hei;
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        
        wid = dis.readInt();
        hei = dis.readInt();
        System.out.println("width:"+wid+"height:"+hei);
           
        
        
            boolean isTrue = true;
            while (isTrue) {
                // Put your code to read the next icon from a server.
                // You don't need to do the ImageIO.write(), ImageIO.read() dance,
                // unless you must save the icon to disk. In that case, you don't need
                // to read it back in.

                // Here, I just rotate the iconInfoList to make it
                // appear as though a new icon was received.
                int len = dis.readInt();
                System.out.println("len:"+len);

                byte[] imageInByte = new byte[len];
                imageInByte = dis.readNBytes(len);
                System.out.println(imageInByte);


                 ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
                BufferedImage bImage2 = ImageIO.read(bis);
              //  Image im1 = bImage2.getScaledInstance(500,500, Image.SCALE_SMOOTH);
               ImageIO.write(bImage2, "jpg", new File("output.jpg") );
             //   bImage2 = ImageIO.read(new File("output.jpg"));
               
            // ImageIcon icon = new ImageIcon(bImage2);
              
                ImageIcon icon = new ImageIcon(imageInByte);
              //  IconInfo iconinfo = new IconInfo(icon);
                //iconInfoList.add(0, iconinfo);
                
               
              //  Collections.rotate(iconInfoList, -1);
                publish(icon);
               
            }
            return null;
        }
 
        @Override
        protected void process(List<Icon> icons) {
         //   IconInfo iconInfo = icons.get(icons.size() - 1);
          //  label.setIcon(iconInfo.icon);
          //  label.setText(iconInfo.name);
         //   label.setSize(iconInfo.dimension);
            
            label.setIcon(icons.get(icons.size() - 1));
        }

      

       
    }

    public static class IconInfo {
      //  final private String name;
         private Icon icon;
         private Dimension dimension;

        IconInfo(Object name) {
        //    this.name = name.toString();
            icon = UIManager.getIcon(name);
            dimension = icon == null
                ? new Dimension(32, 32)
                : new Dimension(icon.getIconWidth(), icon.getIconHeight());
        }
    }

}