
import java.awt.BorderLayout;//borders
import java.awt.Button;//buttons
import java.awt.Checkbox;//checkboxes
import java.awt.CheckboxGroup;//groupingcheckboxes
import java.awt.Color;//colors
import java.awt.Component;//graphical objects
import java.awt.Desktop;//launching web browser
import java.awt.Dimension;//height/width
import java.awt.FlowLayout;//giving components direction
import java.awt.Frame;//popout window
import java.awt.GridLayout;//formatting help (grids)
import java.awt.Insets;//representing the borders
import java.awt.Label;//giving containers labels
import java.awt.Menu;//creating a menu at top
import java.awt.MenuBar;//for making menu
import java.awt.MenuItem;//the options that will be displayed in the menu
import java.awt.Panel;//panels to be formatted with flowlayout
import java.awt.event.ActionEvent;//for answer selection
import java.awt.event.ActionListener;//for recognizing when a user selects an answer
import java.awt.event.FocusEvent;//for components to recognize if pressed or not
import java.awt.event.FocusListener;//recieves input for components to recognize if pressed or not
import java.awt.event.WindowAdapter;//for window event to be used
import java.awt.event.WindowEvent;//to recognize when window status has changed
import java.io.BufferedReader;//to read text questions/answers
import java.io.BufferedWriter;//for writing the text that is read with the reader
import java.io.File;//used to find files
import java.io.FileReader;//used to read files containing questions and answers and selections
import java.io.FileWriter;//to write the report 
import java.io.IOException;//for io exceptions
import java.util.HashMap;//for the hashmaps we will be using

class Show extends Frame implements ActionListener{
     //creating the objects that will be used in the questionaire such as the labels and checkboxes
	private Label lblq;
	private Checkbox choice1;
	private Checkbox choice2;
	private Checkbox choice3;
	private Checkbox choice4;
	private CheckboxGroup cg;
	private Button nextbt;
	private Panel p;
    //creates the different hashmaps that will be used for the questions/answers/selections
	private HashMap<Integer, String>  qchoices;
	private HashMap<Integer, String> qselectedans;
	private HashMap<Integer, String> qcorrectans;
	private int qindex;
	
    //method to make the question/answer input section of quiz
	Show(String title){
        //creating the box that will popup, giving dimensions/color/title
		setTitle(title);
		setLayout(new BorderLayout());
		setSize(new Dimension(500,300));
		setBackground(Color.LIGHT_GRAY);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
        //setting up the file reading
		MenuBar mb=new MenuBar();
		Menu mf=new Menu("File");
		MenuItem iexit=new MenuItem("Exit");
		iexit.addActionListener(this);
		mf.add(iexit);
		mb.add(mf);
		setMenuBar(mb);
        //creating my checkboxes that will be written over with answers with unique strings
		cg=new CheckboxGroup();
		choice1 = new Checkbox("placeholder",false,cg);
		choice2 = new Checkbox("placeholder",false,cg);
		choice3= new Checkbox("placeholder",true,cg);
		choice4 = new Checkbox("placeholder",false,cg);
		p=new Panel();
        //creates the content for the questions and queries for user to select answer/a setup phrase for questions
		p.setLayout(new GridLayout(6,1));
		lblq=new Label("What is...");
		lblq.setForeground(Color.BLUE);
		p.add(lblq);
		p.add(new Label("Select the correct answer:"));
		p.add(choice1);
		p.add(choice2);
		p.add(choice3);
		p.add(choice4);
        //creates the border and background color
		add(p,BorderLayout.CENTER);
		nextbt=new Button("Next");
		nextbt.setBackground(Color.LIGHT_GRAY);
		nextbt.addActionListener(this);
		add(nextbt, BorderLayout.SOUTH);		
		setVisible(true);
		reload();
	}
	
	
    //creating a function to reload my quiz fresh
	public void reload(){
		init();
        //loads the questions and answers from my text files
		loadQchoices("qchoices.txt");
		loadAnswers("qcorrectans.txt");
		nextbt.setLabel("Next");
		moveFirst();
	}
	//function to show or hide the content of the quiz
	public void showCom(boolean hide){
		lblq.setVisible(hide);
		p.setVisible(hide);
		nextbt.setVisible(hide);	
		
	}
	
    //function for moving player along quiz
	public void moveTo(int qin){
		String str=qchoices.get(qin);
        //formats the questions
		String[] parts=str.split("_");		
		lblq.setText(parts[0]);
		choice1.setLabel(parts[1]);
		choice2.setLabel(parts[2]);
		choice3.setLabel(parts[3]);
		choice4.setLabel(parts[4]);
		
	}
    
    //creates 3 hashmaps that will store selected and correct answers
	public void init(){
		qchoices=new HashMap<Integer, String>();
		qselectedans=new HashMap<Integer, String>();
		qcorrectans=new HashMap<Integer, String>();
		
	}
    
    //loads the questions
	public void loadQchoices(String filename){
		//try catch incase no questions are found
		try{
			File f = new File(filename);			
			if(f.exists() && f.length()>0){
				FileReader fr = new FileReader(filename);
				BufferedReader br = new BufferedReader(fr);
				String str="";
				int index=0;
				while((str=br.readLine())!=null){
					qchoices.put(index,str);
					index++;
				}
		
				br.close();
				showCom(true);
			}
			else{
				showCom(false);
			}
		}catch(IOException ie){}
	}
	
    //loads the answers
	public void loadAnswers(String filename){
		//try catch in case no answers are found
		try{
		File f = new File(filename);
		if(f.exists() && f.length()>0){
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String str="";
		int index=0;
		while((str=br.readLine())!=null){
			qcorrectans.put(index,str);
			index++;
		}
		
			br.close();
			showCom(true);
		}
		else{
			showCom(false);
		}
		}catch(IOException ie){}
	}
    
    //moves player to first question
	public void moveFirst(){
		qindex=0;
		moveTo(qindex);
	}
    
    //moves the player from one question to the next
	public void moveNext(){
		addSelectedAnswer();
		if(qindex<qchoices.size()-1){
			qindex++;
			moveTo(qindex);
		}
		
	}
	
    //opens the players web browser and presents selected answers/correct answers
	public void showReport(){
        //try catch to make sure correct webpage format and style is palced
		try{
		FileWriter fw=new FileWriter("qreport.html");
		BufferedWriter bw=new BufferedWriter(fw);
		bw.write("<html><head><title>SS13 Quiz report</title></head>");
		bw.newLine();
		bw.write("<body>");
		bw.write("<h1>Quiz Report</h1>");
		bw.newLine();
		bw.write("<ol>");
		for(int i=0;i<qchoices.size();i++){
            //loads the choices and correct answers
			String str=qchoices.get(i);
			bw.write("<li style='color:#996633;font-size:15pt'>");
			bw.write(str.substring(0,str.indexOf('_')));
			bw.write("<ul style='color:#000055;font-size:14pt'>");
			bw.write("<li>Your answer: "+qselectedans.get(i)+"</li>");
			bw.write("<li>Correct answer: "+qcorrectans.get(i)+"</li>");
			bw.write("</ul>");
			bw.write("</li>");
			
		}
		bw.write("</ol>");
		bw.write("</body>");
		bw.newLine();
		bw.write("</html>");		
		bw.close();
		Desktop dt=Desktop.getDesktop();
		if(Desktop.isDesktopSupported())
			dt.open(new File("qreport.html"));
		}catch(IOException e){}
	}
	
    //for remembering players selected answer
	public void addSelectedAnswer(){
        //creates new checkbox with the selected checkbox adds to the hashmap created earlier
		Checkbox selc=cg.getSelectedCheckbox();
		qselectedans.put(qindex, selc.getLabel());
		
	}
    
    //creating insets to make box look better larger at top shorter on sides medium on bottom
	public Insets getInsets(){
		return new Insets(50,30,15,15);
	}
	
	//recognizes when player is on last question and proceeds to give a generate report option instead of next option
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Next")){
			moveNext();
			if(qindex==qchoices.size()-1)
				nextbt.setLabel("Generate report");
		}
		else if(e.getActionCommand().equals("Generate report")){
			addSelectedAnswer();
			showReport();
		}
		else if(e.getActionCommand().equals("Exit")){
			System.exit(0);
		}
	}
	
	
}
//small class made in same file order to run the game made it in the same file because I thought it would be simpler understand the drawbacks to this system at the same time 
public class SQuiz {
	public static void main(String[] args){
		new Show("Space Station 13 Quiz");
		
	}
}

