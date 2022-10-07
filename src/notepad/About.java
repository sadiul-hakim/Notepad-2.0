package notepad;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class About extends JFrame{
    About(){
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(null);
        this.setSize(500,400);
        this.setLocationRelativeTo(null);
        this.setTitle("About Window");
        ImageIcon frameIcon=new ImageIcon(ClassLoader.getSystemResource("images/Notepad-icon.png"));
        this.setIconImage(frameIcon.getImage());
        
        ImageIcon imageBig=new ImageIcon(ClassLoader.getSystemResource("images/Notepad-icon-big.png"));
        JLabel image=new JLabel(imageBig);
        image.setBounds(100,50,80,80);
        
        String text="""
                    <html>
                    <body>
                    <p>Wellcome to Ultra Ordinary Notepad...</p>
                    <p>This is a Notepad works same as windows Notepad.</p>
                    <p>Here you can create,open,save your text files and </p>
                    <p>format your text.</p>
                    <br/>                    
                    <p style="background:yellow;font-family:Fira Code;">            Developer:                    Sadiul Hakim</p>
                    <p style="background:yellow;font-family:Fira Code;">            Time:                    09 june 2022</p>
                    </body>
                    </html>
                    """;
        JLabel textLabel=new JLabel(text);
        textLabel.setBounds(100,150,400,120);
        
        add(image);
        add(textLabel);
    
    }
    public static void main(String[] args) {
        About frame=new About();
        frame.setVisible(true);
        
    }
}
