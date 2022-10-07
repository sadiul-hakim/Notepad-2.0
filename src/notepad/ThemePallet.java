package notepad;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import org.json.JSONObject;


public class ThemePallet extends JFrame implements ActionListener{
    //data
    private final String[] themes={
        "com.jtattoo.plaf.luna.LunaLookAndFeel",
        "com.jtattoo.plaf.aero.AeroLookAndFeel",
        "com.jtattoo.plaf.texture.TextureLookAndFeel",
        "com.jtattoo.plaf.acryl.AcrylLookAndFeel",
        "com.jtattoo.plaf.mint.MintLookAndFeel",
        "com.jtattoo.plaf.noire.NoireLookAndFeel",
        "com.jtattoo.plaf.mcWin.McWinLookAndFeel"
    };
    private final String[] names={"luna","aero","texture","acryl","mint","noire","mcWin"};
    
    //vars
    private JLabel themeLabel;
    private JList<String> list;
    private JButton apply,ok,cancel;
    private JPanel bottomPanel,centerPanel;
    private JSONObject json;
    
    private  NotePad pad;
    private String writtenText=NotePad.mainArea.getText();
    
    //optionals
        private static Optional<String> selectedName=Optional.ofNullable(null);
        private static Optional<Integer> selectedStyle=Optional.ofNullable(null);
        private static Optional<Integer> selectedSize=Optional.ofNullable(null);
        private static Optional<Color> colorSelected=Optional.ofNullable(null);
        private static Optional<String> defaultTheme=Optional.ofNullable(null);
    
    public ThemePallet(Optional<String> selectedName,Optional<Integer> selectedStyle,Optional<Integer> selectedSize,Optional<Color> colorSelected,Optional<String> defaultTheme){
        //sets
        this.selectedName=selectedName;
        this.selectedStyle=selectedStyle;
        this.selectedSize=selectedSize;
        this.colorSelected=colorSelected;
        this.defaultTheme=defaultTheme;

        //vars
        ImageIcon frameIcon=new ImageIcon(ClassLoader.getSystemResource("images/Apps-preferences-desktop-theme-icon.png"));
        this.setIconImage(frameIcon.getImage());
        this.setTitle("Theme Pallet");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(450,350);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        
        
        handleTheme();
        handleButton();
    }
    
    private void handleTheme(){
        centerPanel=new JPanel();
        centerPanel.setLayout(null);
        
        themeLabel=new JLabel("Select Theme : ");
        themeLabel.setBounds(40,40,150,20);
        list=new JList<>(names);
        list.setBounds(40,80,150,150);
        
        centerPanel.add(themeLabel);
        centerPanel.add(list);
        
        
        add(centerPanel,BorderLayout.CENTER);
    }
    
    private void handleButton(){
        bottomPanel=new JPanel();
        
        ok=new JButton("ok");
        apply=new JButton("apply");
        cancel=new JButton("cancel");
        
        apply.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        bottomPanel.add(ok);
        bottomPanel.add(apply);
        bottomPanel.add(cancel);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        add(bottomPanel,BorderLayout.SOUTH);
    }
    public static void main(String[] args) {
        ThemePallet frame=new ThemePallet(selectedName,selectedStyle,selectedSize,colorSelected,defaultTheme);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==apply){
           
            int index=list.getSelectedIndex();
            Optional<String> theme=Optional.ofNullable(themes[index]);
            
            try{
                Path path=Path.of(ClassLoader.getSystemResource("notepad/settings.json").toURI());
                String text=Files.readString(path);
                json=new JSONObject(text);
                
                
               json.put("Theme",themes[index]);
                
                Files.writeString(path, json.toString());
                

            }catch(Exception ex){
                ex.printStackTrace();
            }
            
            pad=new NotePad(selectedName,selectedStyle,selectedSize,colorSelected,theme);
            
            
        }else if(e.getSource()==cancel){
            list.setSelectedIndex(0);
            
            NotePad pad=new NotePad(selectedName,selectedStyle,selectedSize,colorSelected,defaultTheme);
            NotePad.mainArea.setText(writtenText);
            pad.setVisible(true);
            
            this.dispose();
        }else if(e.getSource()==ok){
            this.dispose();
            NotePad.mainArea.setText(writtenText);
           pad.setVisible(true);
            
        }
    }
}
