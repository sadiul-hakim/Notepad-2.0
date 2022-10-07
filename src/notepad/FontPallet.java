package notepad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import org.json.JSONObject;


public class FontPallet extends JFrame implements ActionListener{
    
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JLabel fontSizeLabel,fontStyleLabel,fontLabel,colorLabel;
    private JButton apply,ok,cancel;
    private JComboBox<String> style;
    private JList<String> fontName;
    private JComboBox<Integer> fontSize;
    private JPanel fontStylePanel;
    private JPanel colorDemo;
    private JButton colorButton;
    private JSONObject json;
    
    private NotePad pad;
    private String writtenText=NotePad.mainArea.getText();
    
    //data
    private final Integer[] sizes={8,12,16,20,24,28,32,36,40};
    private final String[] names={"Fira Code","Open Sans","Roboto","Smooch","Source Code Pro","Arial"};
    
    
    //state
    private Color colorSelected;
    
    //optionals
        private static Optional<String> selectedName=Optional.ofNullable(null);
        private static Optional<Integer> selectedStyle=Optional.ofNullable(null);
        private static Optional<Integer> selectedSize=Optional.ofNullable(null);
        private static Optional<Color> colorPassed=Optional.ofNullable(null);
        private static Optional<String> defaultTheme=Optional.ofNullable(null);

    //cinstructor
    public FontPallet(Optional<String> selectedName,Optional<Integer> selectedStyle,Optional<Integer> selectedSize,Optional<Color> colorSelected,Optional<String> defaultTheme)  {
        this.selectedName=selectedName;
        this.selectedStyle=selectedStyle;
        this.selectedSize=selectedSize;
        this.colorPassed=colorSelected;
        this.defaultTheme=defaultTheme;
        
        this.setSize(600,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("Font");
        this.setResizable(false);
        ImageIcon frameIcon=new ImageIcon(ClassLoader.getSystemResource("images/Fonts-icon.png"));
        this.setIconImage(frameIcon.getImage());
        
        registerFonts();
        
        bottomPanel=new JPanel();
        
        apply=new JButton("apply");
        ok=new JButton("ok");
        cancel=new JButton("cancel");
        
        apply.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(ok);
        bottomPanel.add(apply);
        bottomPanel.add(cancel);
        
        add(bottomPanel,BorderLayout.SOUTH);
        
        handleFontStyles();
       
    }
    
    private void handleFontStyles(){
        fontStylePanel=new JPanel();
        fontStylePanel.setLayout(null);
        
        //size
        fontSizeLabel=new JLabel("Select Font Size : ");
        fontSizeLabel.setBounds(40,40,120,20);
        
        fontSize=new JComboBox<Integer>(sizes);
        fontSize.setSelectedItem(12);
        fontSize.setBounds(170,40,50,20);
        
        //name
        fontLabel=new JLabel("Font Name : ");
        fontLabel.setBounds(40,120,100,20);
        
        fontName=new JList<String>(names);
        fontName.setBounds(40,150,150,200);
        
        //style
        fontStyleLabel=new JLabel("Font Style : ");
        fontStyleLabel.setBounds(250,120,100,20);
        
        style=new JComboBox<String>(new String[]{"Plain","Bold","Italic"});
        style.setBounds(250,150,70,20);
        
        //color
        colorLabel=new JLabel("Font Color : ");
        colorLabel.setBounds(400,120,80,20);
        
        colorDemo=new JPanel();
        colorDemo.setBounds(500,120,50,50);
        colorDemo.setBackground(Color.BLACK);
        
        colorButton=new JButton("Choose");
        colorButton.setBounds(400,150,80,20);
        
        colorButton.addActionListener(this);
        
        //add to panel
        fontStylePanel.add(fontSizeLabel);
        fontStylePanel.add(fontSize);
        fontStylePanel.add(fontLabel);
        fontStylePanel.add(fontName);
        fontStylePanel.add(fontStyleLabel);
        fontStylePanel.add(style);
        fontStylePanel.add(colorLabel);
        fontStylePanel.add(colorDemo);
        fontStylePanel.add(colorButton);
        
        //add to frame
        add(fontStylePanel);
    }
    
    
    private void registerFonts(){
        try{
            Font firaCode=Font.createFont(Font.TRUETYPE_FONT,ClassLoader.getSystemResourceAsStream("fonts/FiraCode-Regular.ttf"));
            Font openSans=Font.createFont(Font.TRUETYPE_FONT,ClassLoader.getSystemResourceAsStream("fonts/OpenSans-Regular.ttf"));
            Font roboto=Font.createFont(Font.TRUETYPE_FONT,ClassLoader.getSystemResourceAsStream("fonts/Roboto-Regular.ttf"));
            Font smooch=Font.createFont(Font.TRUETYPE_FONT,ClassLoader.getSystemResourceAsStream("fonts/Smooch-Regular.ttf"));
            Font sourceCodePro=Font.createFont(Font.TRUETYPE_FONT,ClassLoader.getSystemResourceAsStream("fonts/SourceCodePro-Regular.ttf"));
            
            GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(roboto);
            ge.registerFont(firaCode);
            ge.registerFont(openSans);
            ge.registerFont(smooch);
            ge.registerFont(sourceCodePro);
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public static void main(String[] args) {
        FontPallet frame=new FontPallet(selectedName,selectedStyle,selectedSize,colorPassed,defaultTheme);
        frame.setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
               
        if(e.getSource()==colorButton){
            colorSelected=JColorChooser.showDialog(null, "Color Chooser", Color.black);
            
            colorDemo.setBackground(colorSelected);
            
        }else if(e.getSource()==apply){
            String selectedName=fontName.getSelectedValue();
            int selectedStyle=style.getSelectedIndex();
            int selectedSize=(int) fontSize.getSelectedItem();
            
            try{
                Path path=Path.of(ClassLoader.getSystemResource("notepad/settings.json").toURI());
                String text=Files.readString(path);
                json=new JSONObject(text);
                
                String defaultName=NotePad.initialFontName.get();
                Color defaultColor=NotePad.initialColor.get();
                
                json.put("FontName", selectedName.isEmpty() ? defaultName : selectedName);
                json.put("FontStyle", selectedStyle);
                json.put("FontSize", selectedSize);
                json.put("FontColor",colorSelected == null ? defaultColor :colorSelected.toString());
                
                Files.writeString(path, json.toString());
                

            }catch(Exception ex){
                ex.printStackTrace();
            }
            
            
           
            pad=new NotePad(Optional.ofNullable(selectedName),Optional.ofNullable(selectedStyle),Optional.ofNullable(selectedSize),Optional.ofNullable(colorSelected),defaultTheme);
            
        }else if(e.getSource()==cancel){
            fontName.setSelectedIndex(0);
            style.setSelectedIndex(0);
            fontSize.setSelectedIndex(0);
            colorSelected=Color.black;
            
            NotePad pad=new NotePad(selectedName,selectedStyle,selectedSize,colorPassed,defaultTheme);
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

