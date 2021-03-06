import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class BingoCardBackend {
    public static ArrayList<BingoCard> cards;
    private int cardNum;
    private static int numWins;
    private boolean view;
    private static int seed;
    private static int numDays;
    public static ArrayList<Integer> alr;
    public static ArrayList<Integer> ranArr;
    public static ArrayList<TableObj> tableObjs;
    public static ArrayList<Calendar> cal;
    public static Random run;
    public static int ranPos;

    public BingoCardBackend(int s, int cn, int wn, int nd, boolean b){
        numDays = nd;
        seed = s;
        cardNum = cn;
        numWins = wn;
        view = b;
        cards = new ArrayList<>();
        createCards();
    }

    public void createCards(){
        for(int i=0; i<cardNum;i++){
            BingoCard b = new BingoCard(seed, i+1);
            cards.add(b);
        }
        if(view){PDF(); new BingoCardViewerFrame(cards, seed, numWins);}
        else{PDF(); printSimulator();}
    }

    private void PDF(){
        int complete = cardNum/4;
        new File("Bingo Cards").mkdirs();
        try{
            int pos=0;
            for(int i=0; i<complete; i++){
                BufferedImage image = new BufferedImage(2048, 1536, BufferedImage.TYPE_INT_ARGB);
                Graphics g = image.getGraphics();
                for(int r=0; r<= 700&&pos<cards.size(); r+=700){
                    for(int c=0; c<=700; c+=700 ) {
                        g.drawImage(cards.get(pos).getImage(), c, r, null);
                        pos++;
                    }
                }
                ImageIO.write(image, "png", new File("Bingo Cards/"+(i+1)+ ".png"));
            }
            if(complete*4!=cardNum){
                BufferedImage image = new BufferedImage(2550, 3300, BufferedImage.TYPE_INT_ARGB);
                Graphics g = image.getGraphics();
                for (int r = 0; r <= 700; r += 700) {
                    for (int c = 0; c <= 700&&pos<cards.size(); c += 700) {
                        g.drawImage(cards.get(pos).getImage(), c, r, null);
                        pos++;
                    }
                }
                ImageIO.write(image, "png", new File("Bingo Cards/" + (complete + 1) + ".png"));
            }
        }
        catch(Exception e){e.printStackTrace();}
        try {
            Desktop.getDesktop().open(new File("Bingo Cards"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void findNum(int ran){
        int pos=0;
        for(int i=0; i<cards.size(); i++){
            if(!cards.get(i).getWin()&&cards.get(i).getString().indexOf("" + ran) != -1){
                pos=i;
                int[][] temp = cards.get(pos).getArr();
                for(int r=0; r<temp.length; r++) {
                    ArrayList<Integer> temp1 = new ArrayList<>();
                    for(int a: temp[r]){temp1.add(a);}
                    if (temp1.contains(ran)&&!cards.get(i).getWin()) {
                        cards.get(i).zero(r, temp1.indexOf(ran));
                        markNum(r, temp1.indexOf(ran), pos);
                    }
                }
            }
        }
    }
    public static void markNum(int r, int c, int pos){
        BufferedImage b = cards.get(pos).getImage();
        Graphics g = b.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //System.out.println("R: " + r + " C: " + c);
        r++; c++;
        r=100*r;
        c=100*c;
        g.setColor(new Color(255, 216, 102, 125));
        g.fillOval(c+25, r+25, 50, 50);
        cards.get(pos).changeImage(b);
    }

    public static void markWin(int pos){
        BufferedImage image = cards.get(pos).getImage();
        Graphics g = image.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font font;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new Object() { }.getClass().getClassLoader().getResourceAsStream("carbon.ttf")).deriveFont(160f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            g.setFont(font);
            g.setColor(new Color(255, 216, 102, 125));
            g.drawString("WON", 200, 380);
            cards.get(pos).changeImage(image);

        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    public static String checkWin(int a){
        String s = "";
        if(alr==null) {
            alr = new ArrayList<Integer>();
        }
        for(int i=0; i<cards.size(); i++){
            int[][] temp = cards.get(i).getArr();
            for(int r=0; r<temp.length&&alr.size() < numWins&&!cards.get(i).getWin(); r++){
                int b = i+1;
                if(temp[r][0]+temp[r][1]+temp[r][2]+temp[r][3]+temp[r][4]==0){s += b+ " ";alr.add(i);cards.get(i).setWin(); markWin(i); markNum(2,2,i);}
            }
            for(int c=0; c<5&&alr.size() < numWins&&!cards.get(i).getWin(); c++){
                int b = i+1;
                if(temp[0][c]+temp[1][c]+temp[2][c]+temp[3][c]+temp[4][c]==0){if(!alr.contains(i)){s += b+ " ";alr.add(i);cards.get(i).setWin();markWin(i);markNum(2,2,i);}}
            }
            int b = i+1;
            if(temp[0][0]+temp[1][1]+temp[2][2]+temp[3][3]+temp[4][4]==0&&alr.size() < numWins&&!cards.get(i).getWin()){if(!alr.contains(i)){s += b+ " ";alr.add(i);cards.get(i).setWin();markWin(i);markNum(2,2,i);}}
            if(temp[4][0]+temp[3][1]+temp[2][2]+temp[1][3]+temp[0][4]==0&&alr.size() < numWins&&!cards.get(i).getWin()){if(!alr.contains(i)){s += b+ " ";alr.add(i);cards.get(i).setWin();markWin(i);markNum(2,2,i);}}
        }
        if(!s.isEmpty()){s = a + " " + s;}
        return s;
    }

    public static int getNum(){
        if(ranArr==null){ranArr = new ArrayList<>();}
        if(run==null){run = new Random(seed);}
        int n = run.nextInt(75-1)+1;
        while(!ranArr.contains(n)){
            ranArr.add(n);
            n = run.nextInt(75-1)+1;
        }
       return ranArr.get(ranPos++);
    }


    public static void printSimulator(){
        ArrayList<Recorder> winCards = new ArrayList<>();
        tableObjs = new ArrayList<>();
        cal = new ArrayList<>();
        ArrayList<Integer> arr = new ArrayList<Integer>();
        while(winCards.size()<numWins){
            int n = getNum();
            arr.add(n);
            //System.out.println("Random Num: " + arr.get(arr.size()-1));
            findNum(arr.get(arr.size()-1));
            String s = BingoCardBackend.checkWin(arr.get(arr.size()-1));
            //System.out.println("Check Win: " + s);
            Scanner k = new Scanner(s.substring(s.indexOf(" ") + 1));
            while (k.hasNext() && winCards.size()<numWins) {
                //System.out.println("winCards.add");
                winCards.add(new Recorder(Integer.parseInt(s.substring(0, s.indexOf(" "))),Integer.parseInt(k.next())));
                System.out.println(winCards.get(winCards.size()-1));
            }
        }
        ArrayList<Integer> bPerR = new ArrayList<>(); //int[arr.size()/(numDays*2)];
        for(int a=0; a<numDays*2; a++){bPerR.add(arr.size()/(numDays*2));}
        if(arr.size()%(numDays*2) != 0){
            int i=0;
            int extra = arr.size()%(numDays*2);
            while(extra!=0){
                bPerR.set(i, bPerR.get(i)+1);
                //System.out.println(bPerR.get(i));
                extra--;
                if(i==bPerR.size()-1){i=0;}
                else{i++;}
            }
            for(int a=bPerR.size()-1; a>=0; a--){if(bPerR.get(a)==0){bPerR.remove(a);}}
        }
        System.out.println(arr.size()/(numDays*2));
        System.out.println(bPerR);
        System.out.println("----------------------------------------------------------");
        ArrayList<Integer> temp2 = arr;
            for(int ii=0; ii+1<=bPerR.size()-1; ii+=2){
                ArrayList<Integer> temp = new ArrayList<>();
                ArrayList<Integer> temp1 = new ArrayList<>();
                for(int b=0; b<bPerR.get(ii); b++){
                    temp.add(temp2.remove(0));
                }
                System.out.println(temp);
                System.out.println("----------------------------------------------------------");
                for(int b=0; b<bPerR.get(ii+1); b++){
                    temp1.add(temp2.remove(0));
                }
                System.out.println(temp1);
                System.out.println("Calendar For Loop");
                cal.add(new Calendar((ii/2)+1, temp, temp1));
            }
            String finalFile = "Winners\n" ;
            for(int a=0; a<winCards.size(); a++){
                System.out.println("First For Loop");
                int rn = winCards.get(a).getRanNum();
                int id = winCards.get(a).getCardId();
                for(int b=0; b<cal.size(); b++){
                    System.out.println("Second For Loop");
                    if(cal.get(b).getR1().contains(rn)){System.out.println("R1");finalFile += ("Day: " + cal.get(b).getDay() + " Round 1 "+ " Card: #" + id + "\n");tableObjs.add(new TableObj(cal.get(b).getDay(), 1, id));}
                    else if(cal.get(b).getR2().contains(rn)){System.out.println("R2");finalFile += ("Day: " + cal.get(b).getDay() +" Round 2 "+ " Card: #" + id + "\n");tableObjs.add(new TableObj(cal.get(b).getDay(), 2, id));}
                }
            }
            finalFile += "\n Called Numbers \n";
            for(int a=0; a<cal.size();a++){
                finalFile += "Day: " + cal.get(a).getDay() + " Round 1: ";
                for(int b=0; b<cal.get(a).getR1().size(); b++){
                    finalFile += cal.get(a).getR1().get(b) + " ";
                }
                finalFile += "\nDay: " + cal.get(a).getDay() + " Round 2: ";
                for(int b=0; b<cal.get(a).getR2().size(); b++){
                    finalFile += "" + cal.get(a).getR2().get(b) + " ";
                }
                finalFile += "\n";
            }
        try{PrintWriter output = new PrintWriter(new File("Bingo Cards/printSim.txt"));
            output.println(finalFile);
            output.close();}
        catch(IOException e){}
    }




    public static void simulator(ArrayList<Recorder> rec, int nw){
        ArrayList<TableObj> tableOb = new ArrayList<>();
        cal = new ArrayList<Calendar>();

        ArrayList<Integer> bPerR = new ArrayList<>(); //int[arr.size()/(numDays*2)];
        for(int a=0; a<nw*2; a++){bPerR.add(ranArr.size()/(nw*2));}
        if(ranArr.size()%(nw*2) != 0){
            int i=0;
            int extra = ranArr.size()%(nw*2);
            while(extra!=0){
                bPerR.set(i, bPerR.get(i)+1);
                //System.out.println(bPerR.get(i));
                extra--;
                if(i==bPerR.size()-1){i=0;}
                else{i++;}
            }
            for(int a=bPerR.size()-1; a>=0; a--){if(bPerR.get(a)==0){bPerR.remove(a);}}
        }
        System.out.println(ranArr.size()/(numDays*2));
        System.out.println(bPerR);
        System.out.println("----------------------------------------------------------");
        ArrayList<Integer> temp2 = ranArr;
        for(int ii=0; ii+1<=bPerR.size()-1; ii+=2){
            ArrayList<Integer> temp = new ArrayList<>();
            ArrayList<Integer> temp1 = new ArrayList<>();
            for(int b=0; b<bPerR.get(ii); b++){
                temp.add(temp2.remove(0));
            }
            System.out.println(temp);
            System.out.println("----------------------------------------------------------");
            for(int b=0; b<bPerR.get(ii+1); b++){
                temp1.add(temp2.remove(0));
            }
            System.out.println(temp1);
            System.out.println("Calendar For Loop");
            cal.add(new Calendar((ii/2)+1, temp, temp1));
        }
        String finalFile = "Winners\n" ;
        for(int a=0; a<rec.size(); a++){
            System.out.println("First For Loop");
            int rn = rec.get(a).getRanNum();
            int id = rec.get(a).getCardId();
            for(int b=0; b<cal.size(); b++){
                System.out.println("Second For Loop");
                if(cal.get(b).getR1().contains(rn)){System.out.println("R1");finalFile += ("Day: " + cal.get(b).getDay() + " Round 1 "+ " Card: #" + id + "\n");tableOb.add(new TableObj(cal.get(b).getDay(), 1, id));}
                else if(cal.get(b).getR2().contains(rn)){System.out.println("R2");finalFile += ("Day: " + cal.get(b).getDay() +" Round 2 "+ " Card: #" + id + "\n");tableOb.add(new TableObj(cal.get(b).getDay(), 2, id));}
            }
        }
        finalFile += "\n Called Numbers \n";
        for(int a=0; a<cal.size();a++){
            finalFile += "Day: " + cal.get(a).getDay() + " Round 1: ";
            for(int b=0; b<cal.get(a).getR1().size(); b++){
                finalFile += cal.get(a).getR1().get(b) + " ";
            }
            finalFile += "\nDay: " + cal.get(a).getDay() + " Round 2: ";
            for(int b=0; b<cal.get(a).getR2().size(); b++){
                finalFile += "" + cal.get(a).getR2().get(b) + " ";
            }
            finalFile += "\n";
        }
        try{PrintWriter output = new PrintWriter(new File("Bingo Cards/printSim.txt"));
            output.println(finalFile);
            output.close();}
        catch(IOException e){}
        new SimuTable(tableOb);
    }




}
