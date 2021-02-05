import java.util.*;

class ConnectFour{
    public static int algorithm=0; //1-minimax, 2-alphabeta
    public static int player1=0; //1-human 1st, 2-computer 1st
    public static int human=0; //1-'x', 2-'o'
    public static Board board;
    public static int turn, round=1, total_nodes=0, nodes=0, maxDepth=8;
    public static long start_time, end_time;
    public static boolean valid;
    public static char humanSymbol, aiSymbol;
    public static char config[][]=new char[6][7];
    public static ArrayList<Board> childs = new ArrayList<Board>();
    
    public static Scanner scan = new Scanner(System.in);
    
    public static void main(String args[]){
        char temp='n';
        int i, j;
        System.out.println("********** MENU **********");
        System.out.println();
        System.out.println("Qual algoritmo pretende usar?");
        System.out.println("1) Minimax");
        System.out.println("2) Alphabeta");
        algorithm=scan.nextInt();
        System.out.println();
        
        while(algorithm!=1 && algorithm!=2){
            System.out.println("Resposta inválida!");
            System.out.println("Qual algoritmo pretende usar?");
            System.out.println("1) Minimax");
            System.out.println("2) Alphabeta");
            algorithm=scan.nextInt();
            System.out.println();
        }
        
        System.out.println("Prefere jogar com o 'x' ou com o 'o'?");
        temp=scan.next().charAt(0);
        System.out.println();
        
        while ((temp!='X' && temp!='x' && temp!='O' && temp!='o')){
            System.out.println("Resposta inválida!");
            System.out.println("Prefere jogar com o 'x' ou com o 'o'?");
            temp=scan.next().charAt(0);
            System.out.println();
        }
        
        for(i=0; i<6; i++)
            for(j=0; j<7; j++)
                config[i][j]=' ';
        
        board=new Board(config);
        
        if(temp=='x' || temp=='X'){
            aiSymbol='o';
            humanSymbol='x';
            board.aiSymbol='o';
            board.humanSymbol='x';
        }
        else{
            aiSymbol='x';
            humanSymbol='o';
            board.aiSymbol='x';
            board.humanSymbol='o';
        }
        
        System.out.println("Quem joga primeiro?");
        System.out.println("1) Humano");
        System.out.println("2) IA");
        turn=scan.nextInt();
        System.out.println();
        
        while ((turn!=1 && turn!=2)){
            System.out.println("Quem joga primeiro?");
            System.out.println("1) Humano");
            System.out.println("2) IA");
            turn=scan.nextInt();
            System.out.println();
        }
        
        start();
        
        if(board.whoWon==1){
            System.out.println("Parabéns! Venceu o jogo!");
            System.out.println();
            board.print();
        }
        else if(board.whoWon==2){
            System.out.println("A IA venceu o jogo. Tente novamente.");
            System.out.println();
            board.print();
        }
        else if(board.whoWon==-1){
            System.out.println("Empate!");
            System.out.println();
            board.print();
        }
        System.out.println("Nº de nós total = "+total_nodes);
            
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // START
    static void start(){
        int col=-1, i, j;
        long time;
        
        while(!board.checkBoard(aiSymbol) && !board.checkBoard(humanSymbol) && !board.draw()){
            if(turn==1){ // Vez do humano
                board.print();
                System.out.println("Em qual coluna pretende jogar?");
                col=scan.nextInt()-1;
                System.out.println();
                
                if(col>=0 && col<7)
                    valid=board.play(col, humanSymbol);
                
                while(col<0 || col>6 || !valid){
                    System.out.println("Resposta inválida!");
                    System.out.println("Em qual coluna pretende jogar?");
                    col=scan.nextInt()-1;
                    valid=board.play(col, humanSymbol);
                }
                board.update(aiSymbol);
                board.print();
                System.out.println();
                turn=2;
            }
            else{
                nodes=0;
                System.out.println("Vez da IA!");
                System.out.println();
                start_time = System.nanoTime();
                switch (algorithm) {
                    case 1: // Minimax
                        board.tab=minimax(board);
                        board.update(aiSymbol);
                        break;
                    case 2:
                        board.tab=alphabeta(board);
                        board.update(aiSymbol);
                        break;
                }
                end_time = System.nanoTime();
                turn=1;
                time=(end_time-start_time)/1000000;
            System.out.printf("A jogada demorou %d ms e expandiu %d nós!",time, nodes);
                System.out.println();
                System.out.println();
            }
            round++;
        }
        
    }
    
    
    ////////////////////////////////////////////////////////
    // Gera filhos
    static ArrayList <Board> expand(Board b, char player){
        int i;
        ArrayList <Board> c=new ArrayList<Board>();
        for(i=0; i<7; i++){
            Board temp=new Board(b.tab);
            if(temp.play(i, player)){
                temp.update(aiSymbol);
                temp.util=temp.utility();
                c.add(temp);
            }
        }
        return c;
    }
    
    
    ////////////////////////////////////////////////////////////////////
    // MINIMAX
    static char[][] minimax(Board b){
        int value, i=0, max=Integer.MIN_VALUE;
        childs.clear();
        Board temp=null;
        value=maximize(b, 1, true);
        for(Board c: childs){
            i++;
            c.update(aiSymbol);
           if(c.win(aiSymbol)){ // verifica se ganhou
                c.util=1000;
                max=c.util;
                temp=c;
            }
            if(c.threat()==true){
                c.util=-1000;
            }
            if(c.util>max){
                max=c.util;
                temp=c;
            }
        }
        return temp.tab;
    }
    
    // MAXIMIZE
    static int maximize(Board b, int depth, boolean add){
        total_nodes++;
        nodes++;
        int val=Integer.MIN_VALUE;
        b.update(aiSymbol);
        if(depth==maxDepth || b.end || b.draw()){
            return b.utility();
        }
        if(add){
            childs=expand(b,aiSymbol);
            for(Board c: childs){
                val=minimize(c, depth+1);
                c.util=val;
            }
        }
        else{
            for(Board c: expand(b, aiSymbol)){
                val=Math.max(val, minimize(c,depth+1));
                c.util=val;
            }
        }
        return val; 
    }
    
    // MINIMIZE
    static int minimize(Board b, int depth){
        total_nodes++;
        nodes++;
        int val=Integer.MAX_VALUE;
        b.update(aiSymbol);
        if(depth==maxDepth || b.end){
            return b.utility();
        }
        for(Board c: expand(b, humanSymbol)){
            val=Math.min(val, maximize(c,depth+1,false));
            c.util=val;
        }  
        return val;
    }
        
        
    /////////////////////////////////////////////////////////////////////
    // ALPHABETA
    static char[][] alphabeta(Board b){
        int value, i=0, max=Integer.MIN_VALUE;
        childs.clear();
        Board temp=null;
        value=maximize_ab(b, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        for(Board c: childs){
            i++;
            c.update(aiSymbol);
            if(c.win(aiSymbol)){ // verifica se ganhou
                c.util=1000;
                max=c.util;
                temp=c;
            }
            if(c.threat()==true){
                c.util=-1000;
            }
            if(c.util>max){
                max=c.util;
                temp=c;
            }
        }
        return temp.tab;
    }
    
    // MAXIMIZE ALPHA-BETA
    static int maximize_ab(Board b, int depth, int alfa, int beta, boolean add){
        total_nodes++;
        nodes++;
        int val=Integer.MIN_VALUE;
        b.update(aiSymbol);
        if(b.end || depth==maxDepth){
            return b.utility();
        }
        if(add){
            childs=expand(b,aiSymbol);
            for(Board c: childs){
                val=minimize_ab(c, depth+1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                c.util=val;
            }
        }
        else{
            for(Board c: expand(b, aiSymbol)){
                val=Math.max(val, minimize_ab(c, depth+1, alfa, beta));
                c.util=val;
                alfa=Math.max(alfa,val);
                if(beta<=alfa)
                    return val;
            }
        }
        return val;
    } 
    
    // MINIMIZE ALPHA-BETA
    static int minimize_ab(Board b, int depth, int alfa, int beta){
        total_nodes++;
        nodes++;
        int val=Integer.MAX_VALUE;
        b.update(aiSymbol);
        if(b.end || depth==maxDepth){
            return b.utility();
        }
        for(Board c: expand(b, humanSymbol)){
            val=Math.min(val, maximize_ab(c, depth+1, alfa, beta, false));
            c.util=val;
            beta=Math.min(beta,val);
            if(beta<=alfa)
                return val;    
        }  
        return val;
    }
}











class Board{
    char humanSymbol;
    char aiSymbol;
    boolean end; // Em caso de alguém ganhar ou empate
    int whoWon; // 1-player, 2-AI
    int util; // Função utilidade
    char tab[][]; //configuraçao do tabuleiro
    Board(char m[][]){
        tab=new char[6][7];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)
                tab[i][j] = m[i][j];
        if(checkBoard(humanSymbol)==true){
            end=true;
            whoWon=1;
        }
        else if(checkBoard(aiSymbol)==true){
            end=true;
            whoWon=2;
        }
        else if(draw()){
            end=true;
            whoWon=-1;
        }
        else{
            end=false;
            whoWon=0;
        }
    }
    
    
    ///////////////////////////////////////////////////////
    // Verifica se AI venceu
    boolean win(char symbol){
        if(checkBoard(symbol))
            return true;
        else
            return false;
    }
    
    
    ///////////////////////////////////////////////////////
    // Detecta ameaça na proxima jogada
    boolean threat(){      
        int i, j;
        // DIAGONAL PRINCIPAL
        for(i=0; i<6 && i+1<6 && i+2<6 && i+3<6; i++){
            for(j=0; j<7 && j+1<7 && j+2<7 && j+3<7; j++){
                // _XXX
                if(tab[i][j]==' ' && tab[i+1][j+1]==humanSymbol && tab[i+2][j+2]==humanSymbol && tab[i+3][j+3]==humanSymbol && tab[i+1][j]!=' ')
                    return true;
                // X_XX
                if(tab[i][j]==humanSymbol && tab[i+1][j+1]==' ' && tab[i+2][j+2]==humanSymbol && tab[i+3][j+3]==humanSymbol && tab[i+2][j+1]!=' ')
                    return true;
                // XX_X
                if(tab[i][j]==humanSymbol && tab[i+1][j+1]==humanSymbol && tab[i+2][j+2]==' ' && tab[i+3][j+3]==humanSymbol && tab[i+3][j+2]!=' ')
                    return true;
                // XXX_
                if(tab[i][j]==humanSymbol && tab[i+1][j+1]==humanSymbol && tab[i+2][j+2]==humanSymbol && tab[i+3][j+3]==' ' && (((i+4<6 && tab[i+4][j+3]!=' ') || (i+3)==5)))
                    return true;
            }
        }

        
        
        // DIAGONAL SECUNDÁRIA
        for(i=0;i<6; i++){
            for(j=6; j>=0 && j-1>=0 && j-2>=0 && j-3>=0; j--){
                // XXX_
                if((i+1<6 && i+2<6 && i+3<6) && tab[i][j]==' ' && tab[i+1][j-1]==humanSymbol && tab[i+2][j-2]==humanSymbol && tab[i+3][j-3]==humanSymbol && tab[i+1][j]!=' ')
                    return true;
                // X_XX
                if((i+1<6 && i+2<6 && i+3<6) && tab[i][j]==humanSymbol && tab[i+1][j-1]==' ' && tab[i+2][j-2]==humanSymbol && tab[i+3][j-3]==humanSymbol && tab[i+2][j-1]!=' ')
                    return true;
                // XX_X
                if((i+1<6 && i+2<6 && i+3<6) && tab[i][j]==humanSymbol && tab[i+1][j-1]==humanSymbol && tab[i+2][j-2]==' ' && tab[i+3][j-3]==humanSymbol && tab[i+3][j-2]!=' ')
                    return true;
                // _XXX
                if((i+1<6 && i+2<6 && i+3<6) && tab[i][j]==humanSymbol && tab[i+1][j-1]==humanSymbol && tab[i+2][j-2]==humanSymbol && tab[i+3][j-3]==' ' && (((i+4<6 && tab[i+4][j-3]!=' ') || (i+3)==5)))
                    return true;
            }
        } 

        
        // LINHAS
        for(i=0; i<6; i++){
            for(j=0; j<7; j++){
                // _XXX
                if((j+1)<7 && (j+2)<7 && (j+3)<7 && tab[i][j]==' ' && tab[i][j+1]==humanSymbol && tab[i][j+2]==humanSymbol && tab[i][j+3]==humanSymbol && (((i+1)<6 && tab[i+1][j]!=' ') || i==5))
                    return true;
                // X_XX
                if((j+1)<7 && (j+2)<7 && (j+3)<7 && tab[i][j]==humanSymbol && tab[i][j+1]==' ' && tab[i][j+2]==humanSymbol && tab[i][j+3]==humanSymbol && (((i+1)<6 && tab[i+1][j+1]!=' ') || i==5))
                    return true;
                // XX_X
                if((j+1)<7 && (j+2)<7 && (j+3)<7 && tab[i][j]==humanSymbol && tab[i][j+1]==humanSymbol && tab[i][j+2]==' ' && tab[i][j+3]==humanSymbol && (((i+1)<6 && tab[i+1][j+2]!=' ') || i==5))
                    return true;
                // XXX_
                if((j+1)<7 && (j+2)<7 && (j+3)<7 && tab[i][j]==humanSymbol && tab[i][j+1]==humanSymbol && tab[i][j+2]==humanSymbol && tab[i][j+3]==' ' && (((i+1)<6 && tab[i+1][j+3]!=' ') || i==5))
                    return true;
                
            }

        }   

        
        int count=0;
        // COLUNAS MIN
        for(i=0; i<7; i++){
            for(j=5; j>=0; j--){
                if(tab[j][i]==humanSymbol){
                    count++;
                    if(count==3 && (j-1)>=0 && tab[j-1][i]==' ')
                        return true; 
                }
                else
                    count=0;
            }
            count=0;
        }
        return false;
    }         

    
    
    //////////////////////////////////////////////////
    // UPDATE
    int update(char ai){
        if(ai=='x'){
            aiSymbol='x';
            humanSymbol='o';
        }
        else{
            humanSymbol='x';
            aiSymbol='o';
        }
        if(checkBoard(humanSymbol)){
            end=true;
            whoWon=1;
        }
        else if(checkBoard(aiSymbol)){
            end=true;
            whoWon=2;
        }
        else if(draw()){
            end=true;
            whoWon=-1;
        }
        else
            end=false;
        return whoWon;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Verifica se alguém ganhou
	boolean checkBoard(char player){
        int i, j=0, count=0;
        
        // Linhas
        for(i=0; i<6; i++){
            for(j=0; j<7; j++){
                if(tab[i][j]==player){
                    count++;
                    if(count==4)
                        return true;
                }
                else
                    count=0;
            }
            count=0;
        }
        
        // Colunas
        for(i=0; i<7; i++){
            for(j=0; j<6; j++){
                if(tab[j][i]==player){
                    count++;
                    if(count==4)
                        return true;
                }
                else
                    count=0;
            }
            count=0;
        }
        
        // Diagonal Principal
        for(i=0; i<6 && i+1<6 && i+2<6 && i+3<6; i++){
            for(j=0; j<7 && j+1<7 && j+2<7 && j+3<7; j++){
                if(tab[i][j]==player && tab[i+1][j+1]==player && tab[i+2][j+2]==player && tab[i+3][j+3]==player)
                    return true;
            }
        }
        
        // Diagonal Secundária
        for(i=0;i<6 && i+1<6 && i+2<6 && i+3<6; i++){
            for(j=6; j>=0 && j-1>=0 && j-2>=0 && j-3>=0; j--){
                if(tab[i][j]==player && tab[i+1][j-1]==player && tab[i+2][j-2]==player && tab[i+3][j-3]==player){
                    return true;
                }
            }
        } 
        if(draw())
            return true;
        return false;
	} // Fim checkBoard
    
    /////////////////////////////////////////////////////
    // Imprime o estado do tabuleiro - NAO MEXER
    void print(){
        int i, j;
        for(i=0; i<6; i++){   
            for(j=0; j<7; j++){
                System.out.print("|"+tab[i][j]);
            }
            System.out.println("|");
        }
        System.out.println("---------------");
        System.out.println(" 1 2 3 4 5 6 7 ");
        System.out.println();
    }
    
    /////////////////////////////////////////////////////
    // Marca posição como jogada - NAO MEXER
    boolean play(int col, char symbol){
        int i=5;
        // Coluna está cheia
        if(tab[0][col]!=' ')
            return false;
        // Guarda jogada no tabuleiro
        for(i=5; i>=0; i--){
            if(tab[i][col]==' '){
                tab[i][col]=symbol;
                return true;
            }
        }
        return false;
    }
    
    ///////////////////////////////////////////////////////
    // Verifica empates
    boolean draw(){
        int j;
        for(j=0; j<7; j++){
            if(tab[0][j]==' ')
                return false;
        }
        return true;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    // Função de utilidade
    int utility(){
        int max, min;
        max=eval(aiSymbol);
        min=eval(humanSymbol);
        return max-min;
    }
    
    // Avaliação
    int eval(char maxS){
        int i, j, k=5, value_max=0;
        
        if(draw()){
            value_max=0;
            return value_max;
        }

        // DIAGONAL PRINCIPAL
        for(i=0; i<6 && i+1<6 && i+2<6 && i+3<6; i++){
            for(j=0; j<7 && j+1<7 && j+2<7 && j+3<7; j++){
                // XXXX
                if(tab[i][j]==maxS && tab[i+1][j+1]==maxS && tab[i+2][j+2]==maxS && tab[i+3][j+3]==maxS){
                    value_max=512;
                    return value_max;
                }
                
                // _XXX
                if(tab[i][j]==' ' && tab[i+1][j+1]==maxS && tab[i+2][j+2]==maxS && tab[i+3][j+3]==maxS && tab[i+1][j]!=' ')
                    value_max+=50;
                // X_XX
                if(tab[i][j]==maxS && tab[i+1][j+1]==' ' && tab[i+2][j+2]==maxS && tab[i+3][j+3]==maxS && tab[i+2][j+1]!=' ')
                    value_max+=50;
                // XX_X
                if(tab[i][j]==maxS && tab[i+1][j+1]==maxS && tab[i+2][j+2]==' ' && tab[i+3][j+3]==maxS && tab[i+3][j+2]!=' ')
                    value_max+=50;
                // XXX_
                if(tab[i][j]==maxS && tab[i+1][j+1]==maxS && tab[i+2][j+2]==maxS && tab[i+3][j+3]==' ' && (((i+4<6 && tab[i+4][j+3]!=' ') || (i+3)==5)))
                    value_max+=50;
                
                // _XX
                if(tab[i][j]==' ' && tab[i+1][j+1]==maxS && tab[i+2][j+2]==maxS && tab[i+1][j]!=' ')
                    value_max+=10;
                // X_X
                if(tab[i][j]==maxS && tab[i+1][j+1]==' ' && tab[i+2][j+2]==maxS && tab[i+2][j+1]!=' ')
                    value_max+=10;
                // XX_
                if(tab[i][j]==maxS && tab[i+1][j+1]==maxS && tab[i+2][j+2]==' ' && tab[i+3][j+2]!=' ')
                    value_max+=10;

                
                // _X
                if(tab[i][j]==' ' && tab[i+1][j+1]==maxS && tab[i+1][j]!=' ')
                    value_max+=1;
                // X_
                if(tab[i][j]==maxS && tab[i+1][j+1]==' ' && tab[i+2][j+1]!=' ')
                    value_max+=1;
            }
        }
        
        
        // DIAGONAL SECUNDÁRIA
        for(i=0;i<6 && i+1<6 && i+2<6 && i+3<6; i++){
            for(j=6; j>=0 && j-1>=0 && j-2>=0 && j-3>=0; j--){
                // XXXX
                if(tab[i][j]==maxS && tab[i+1][j-1]==maxS && tab[i+2][j-2]==maxS && tab[i+3][j-3]==maxS){
                    value_max=512;
                    return value_max;
                }
                
                // XXX_
                if(tab[i][j]==' ' && tab[i+1][j-1]==maxS && tab[i+2][j-2]==maxS && tab[i+3][j-3]==maxS && tab[i+1][j]!=' ')
                    value_max+=50;
                // XX_X
                if(tab[i][j]==maxS && tab[i+1][j-1]==' ' && tab[i+2][j-2]==maxS && tab[i+3][j-3]==maxS && tab[i+2][j-1]!=' ')
                    value_max+=50;
                // X_XX
                if(tab[i][j]==maxS && tab[i+1][j-1]==maxS && tab[i+2][j-2]==' ' && tab[i+3][j-3]==maxS && tab[i+3][j-2]!=' ')
                    value_max+=50;
                // _XXX
                if(tab[i][j]==maxS && tab[i+1][j-1]==maxS && tab[i+2][j-2]==maxS && tab[i+3][j-3]==' ' && (((i+4<6 && tab[i+4][j-3]!=' ') || (i+3)==5)))
                    value_max+=50;
                
                // XX_
                if(tab[i][j]==' ' && tab[i+1][j-1]==maxS && tab[i+2][j-2]==maxS && tab[i+1][j]!=' ')
                    value_max+=10;
                // X_X
                if(tab[i][j]==maxS && tab[i+1][j-1]==' ' && tab[i+2][j-2]==maxS && tab[i+3][j-3]==maxS && tab[i+2][j-1]!=' ')
                    value_max+=10;
                // _XX
                if(tab[i][j]==maxS && tab[i+1][j-1]==maxS && tab[i+2][j-2]==' ' && tab[i+3][j-2]!=' ')
                    value_max+=10;
                
                // X_
                if(tab[i][j]==' ' && tab[i+1][j-1]==maxS && tab[i+1][j]!=' ')
                    value_max+=1;
                // _X
                if(tab[i][j]==maxS && tab[i+1][j-1]==' ' && tab[i+2][j-1]!=' ')
                    value_max+=1;
            }
        } 
        
        // LINHAS
        for(i=0; i<6; i++){
            for(j=0; j<7; j++){
                // XXXX
                if((j+1)<7 && (j+2)<7 && (j+3)<7 && tab[i][j]==maxS && tab[i][j+1]==maxS && tab[i][j+2]==maxS && tab[i][j+3]==maxS){
                    value_max=512;
                    return value_max;
                }
                
                // _XXX
                if((j+1)<7 && (j+2)<7 && (j+3)<7 && tab[i][j]==' ' && tab[i][j+1]==maxS && tab[i][j+2]==maxS && tab[i][j+3]==maxS && (((i+1)<6 && tab[i+1][j]!=' ') || i==5))
                    value_max+=50;
                // X_XX
                if((j+1)<7 && (j+2)<7 && (j+3)<7 && tab[i][j]==maxS && tab[i][j+1]==' ' && tab[i][j+2]==maxS && tab[i][j+3]==maxS && (((i+1)<6 && tab[i+1][j+1]!=' ') || i==5))
                    value_max+=50;
                // XX_X
                if((j+1)<7 && (j+2)<7 && (j+3)<7 && tab[i][j]==maxS && tab[i][j+1]==maxS && tab[i][j+2]==' ' && tab[i][j+3]==maxS && (((i+1)<6 && tab[i+1][j+2]!=' ') || i==5))
                    value_max+=50;
                // XXX_
                if((j+1)<7 && (j+2)<7 && (j+3)<7 && tab[i][j]==maxS && tab[i][j+1]==maxS && tab[i][j+2]==maxS && tab[i][j+3]==' ' && (((i+1)<6 && tab[i+1][j+3]!=' ') || i==5))
                    value_max+=50;
                
                // _XX
                if((j+1)<7 && (j+2)<7 && tab[i][j]==' ' && tab[i][j+1]==maxS && tab[i][j+2]==maxS && (((i+1)<6 && tab[i+1][j]!=' ') || i==5))
                    value_max+=10;
                 // X_X
                if((j+1)<7 && (j+2)<7 && tab[i][j]==maxS && tab[i][j+1]==' ' && tab[i][j+2]==maxS && (((i+1)<6 && tab[i+1][j+1]!=' ') || i==5))
                    value_max+=10;
                 // XX_
                if((j+1)<7 && (j+2)<7 && tab[i][j]==maxS && tab[i][j+1]==maxS && tab[i][j+2]==' ' && (((i+1)<6 && tab[i+1][j+2]!=' ') || i==5))
                    value_max+=10;
                
                 // _X
                if((j+1)<7 && tab[i][j]==' ' && tab[i][j+1]==maxS && (((i+1)<6 && tab[i+1][j]!=' ') || i==5))
                    value_max+=1;
                 // X_
                if((j+1)<7 && tab[i][j]==maxS && tab[i][j+1]==' ' && (((i+1)<6 && tab[i+1][j+1]!=' ') || i==5))
                    value_max+=1;
            }
        }
        
        int count=0;
        // COLUNAS MAX
        for(i=0; i<7; i++){
            for(j=5; j>=0; j--){
                if(tab[j][i]==maxS){
                    count++;
                    if(count==1 && (i-1)>=0 && tab[i-1][j]==' ')
                        value_max+=1;
                    if(count==2 && (i-1)>=0 && tab[i-1][j]==' ')
                        value_max+=10;
                    if(count==3 && (i-1)>=0 && tab[i-1][j]==' ')
                        value_max+=50; 
                    if(count==4 && (i-1)>=0 && tab[i-1][j]==' '){
                        value_max=512;
                        return value_max;
                    }
                }
                else
                    count=0;
            }
            count=0;
        }
        
        return value_max;
    }
}
