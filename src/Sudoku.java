import java.util.*;
import java.io.*;
import java.lang.Character;


class Sudoku
{
    
    int SIZE, N;
    int Grid[][];
    
    
    //My algorithm implements two different approaches to obtain a solution in the least possible time.
    //First it fills all the obvious choices in the grid. This is done by the method fillPossibilites()
    //Obvious choices are spaces where only one number can be placed. If an obvious choice is found,
    //the method is called again to check if that space opened a new obvious choice.
    
    //Then it looks for the most constrained unfilled positions, assigns a number and keeps solving.
    //If a mistake is made, it undoes the move and tries a different option.
    //By targeting the constrained cells first most of the collisions occur early in the execution,
    //thus arriving at a solution in significantly less time than a brute-force back-tracking algorithm would.
    
    //This approach is known as a most-Constrained-First approach.
    
    
    public boolean solve(){
    	
    	//fill boxes until the no obvious positions are generated
    	while(this.fillPossibilites()){}
    	
    	//call the solve method that'll execute the most-constrained back-tracking algorithm
    	return this.solve2();
    }
    
    
    public boolean solve2(){

		boolean solved = false;
		
		print();
		System.out.println();
		
		int row = -1;
		int col = -1;
		ArrayList<Integer> possibilities = null;
				
		//find the cell with the least possibilities
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				if(Grid[i][j] == 0){
					
					ArrayList<Integer> newPossibilities = getPossibilities(this, i , j);
					if(row < 0 || newPossibilities.size() < possibilities.size()){
						row = i;
						col = j;
						possibilities = newPossibilities;
					}
					
				}
			}
		}
		
		//if there aren't any empty cells, the puzzle has been solved;
		if(row < 0){
			solved = true;
		}else{
			
			for(int i = 0; i < possibilities.size(); i++){
				
				//Array to store current grid in case the program has to backtrack
				int temp[][] = new int[N][N];			
				for (int j = 0; j < Grid.length; j++) 
				  System.arraycopy(Grid[j], 0, temp[j], 0, Grid[0].length);
				
				//Try one of the possibilities allowed for the given row and col, call solve() again.
				Grid[row][col] = possibilities.get(i);
				if(this.solve2()){
					solved = true;
					break;
				}
				//if there is a mistake, undo the moves and let the loop try a different number.
				//reassigns Grid to the the previously used board
				Grid = temp;
			}
		}
		
		return solved;
    }

	//stores the possible placements of a given space in the Sudoku grid in an ArrayList, returns it.
    public static ArrayList<Integer> getPossibilities(Sudoku s, int row, int col){
    	ArrayList<Integer> candidates = new ArrayList<Integer>();

        for (int c = 1; c <= s.N; c++){

            boolean collision = false;
            Character.getNumericValue(c);
            
            //check if the number already exists in the column
            for (int i = 0; i < s.N; i++){
                if((s.Grid[row][i] == c || 
    		        s.Grid[i][col] == c))
                {
                    collision = true;
                    break;
                }
            }
            if(!collision){
            	//check if the number already exists in the box
	            for (int i = 0; i < s.SIZE; i++){
	             	for (int j = 0; j < s.SIZE; j++){
	             		if (s.Grid[i+(row-row%s.SIZE)][j+(col-col%s.SIZE)] == c){
	             			collision = true;
	             			break;
	             		}
	             	}
	            }
            }
            //if there aren't collision, the number 'c' becomes a possibility
            if (!collision) candidates.add(c);
            

        }
        return candidates;
    }
    
    //Modified version of getPossibilities that returns the candidates for a given space.
    //If the space being considered has more than one possibility, it saves time by not executing
    //the entire method.
    //This is helper method for fillPossibilites()
    public static ArrayList<Integer> getPossibilitiesCustom(Sudoku s, int row, int col){
    	ArrayList<Integer> candidates = new ArrayList<Integer>();

        for (int c = 1; c <= s.N; c++){

            boolean collision = false;
            Character.getNumericValue(c);
            
            for (int i = 0; i < s.N; i++){
                if((s.Grid[row][i] == c || 
    		        s.Grid[i][col] == c))
                {
                    collision = true;
                    break;
                }
            }
            if(!collision){
	            for (int i = 0; i < s.SIZE; i++){
	             	for (int j = 0; j < s.SIZE; j++){
	             		if (s.Grid[i+(row-row%s.SIZE)][j+(col-col%s.SIZE)] == c){
	             			collision = true;
	             			break;
	             		}
	             	}
	            }
            }
            if (!collision) candidates.add(c);
            
            //This conditional is the time saver for this method.
            if (candidates.size() > 1){
            	return candidates;
            }
            

        }
        return candidates;
    }
        
    
    //Finds the spaces in the Sudoku grid where only one number can be placed
    //The change variable is used to tell when the Grid remains unchanged, so that the
    //method isn't called more than it is necessary
    public boolean fillPossibilites(){
    	
    	boolean change = false;
    	
    	//checks the entire Grid for spaces with only one possibility.
    	for(int i = 0; i < N; i++){
    		for(int j = 0; j < N; j++){
    			
    			if(Grid[i][j] == 0){
    				ArrayList<Integer> newPossibilities = getPossibilitiesCustom(this, i , j);
    				if(newPossibilities.size() > 1) continue;
    				Grid[i][j] = newPossibilities.get(0);
    				change = true;
    				
    			}
    		}
    		    		
    	}
    	
		return change;

    	
    }
    
    //THE CODE BELOW was written by prof. Mathieu Blanchette for McGill University.
    //All of the above was written by me, Daniel Macario.
    /*****************************************************************************/
    /* NOTE: YOU SHOULD NOT HAVE TO MODIFY ANY OF THE FUNCTIONS BELOW THIS LINE. */
    /*****************************************************************************/
 
    /* Default constructor.  This will initialize all positions to the default 0
     * value.  Use the read() function to load the Sudoku puzzle from a file or
     * the standard input. */
    
    public Sudoku( int size )
    {
        SIZE = size;
        N = size*size;
        Grid = new int[N][N];
        for( int i = 0; i < N; i++ ){ 
            for( int j = 0; j < N; j++ ){
            	Grid[i][j] = 0; 
            }     
        }
    }
    
    
    static int readInteger( InputStream in ) throws Exception
    {
        int result = 0;
        boolean success = false;

        while( !success ) {
            String word = readWord( in );

            try {
                result = Integer.parseInt( word );
                success = true;
            } catch( Exception e ) {
                // Convert 'x' words into 0's
                if( word.compareTo("x") == 0 ) {
                    result = 0;
                    success = true;
                }
                // Ignore all other words that are not integers
            }
        }

        return result;
    }


    /* readWord is a helper function that reads a word separated by white space. */
    static String readWord( InputStream in ) throws Exception
    {
        StringBuffer result = new StringBuffer();
        int currentChar = in.read();
        String whiteSpace = " \t\r\n";
        // Ignore any leading white space
        while( whiteSpace.indexOf(currentChar) > -1 ) {
            currentChar = in.read();
        }

        // Read all characters until you reach white space
        while( whiteSpace.indexOf(currentChar) == -1 ) {
            result.append( (char) currentChar );
            currentChar = in.read();
        }
        return result.toString();
    }
    
    public void read( InputStream in ) throws Exception
    {
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                Grid[i][j] = readInteger( in );
            }
        }
    }
    
    void printFixedWidth( String text, int width )
    {
        for( int i = 0; i < width - text.length(); i++ )
            System.out.print( " " );
        System.out.print( text );
    }

    
    public void print()
    {
        // Compute the number of digits necessary to print out each number in the Sudoku puzzle
        int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;

        // Create a dashed line to separate the boxes 
        int lineLength = (digits + 1) * N + 2 * SIZE - 3;
        StringBuffer line = new StringBuffer();
        for( int lineInit = 0; lineInit < lineLength; lineInit++ )
            line.append('-');

        // Go through the Grid, printing out its values separated by spaces
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                printFixedWidth( String.valueOf( Grid[i][j] ), digits );
                // Print the vertical lines between boxes 
                if( (j < N-1) && ((j+1) % SIZE == 0) )
                    System.out.print( " |" );
                System.out.print( " " );
            }
            System.out.println();

            // Print the horizontal line between boxes
            if( (i < N-1) && ((i+1) % SIZE == 0) )
                System.out.println( line.toString() );
        }
    }


    /* The main function reads in a Sudoku puzzle from the standard input, 
     * unless a file name is provided as a run-time argument, in which case the
     * Sudoku puzzle is loaded from that file.  It then solves the puzzle, and
     * outputs the completed puzzle to the standard output. */
    public static void main( String args[] ) throws Exception
    {
        InputStream in;
        if( args.length > 0 ) 
            in = new FileInputStream( args[0] );
        else
            in = System.in;

        // The first number in all Sudoku files must represent the size of the puzzle.  See
        // the example files for the file format.
        int puzzleSize = readInteger( in );
        if( puzzleSize > 100 || puzzleSize < 1 ) {
            System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
            System.exit(-1);
        }

        Sudoku s = new Sudoku( puzzleSize );
        // read the rest of the Sudoku puzzle
        s.read( in );
        
        
        s.solve();
        s.print();
    }
}

