/*
Written by no_data_here <error.404.no.data.here@gmail.com> on 02/25/2018 
Last edited on 02/26/2018.
Status as of 02/26/2018: INCOMPLETE

Copyright (C) 2018 no_data_here

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;

public class RotaryScores {
	
	static int[] config= {0,0,0};//array for holding number of judges, essays, and places
	static int[] tiebreakerPlaces;//if this is 1, there was a tie that had to be broken by the number of rankings, if it's 2, a manual runoff is needed, 3: both kinds
	
	private static void getConfig(Scanner input) {//read Config.txt for config values
		//temporary input from stdin to collect the config values
		System.out.print("Input the number of judges: ");
		config[0]=input.nextInt();
		System.out.print("Input the number of essays: ");
		config[1]=input.nextInt();
		System.out.print("Input the number of places to award: ");
		config[2]=input.nextInt();
		tiebreakerPlaces = new int[config[2]];//set tiebreakerPlaces to size for checking if a given place needed a tiebreaker
	}
	
	private static int[] readBallot(Scanner input) {//read a given ballot for the scores given by the judges
		int[] scores = new int[config[2]];
		//temporary input to get score from stdin
		for(int x = 0; x < config[2]; x++) {
			int id=0;
			while(id <= 0 || id > config[1]) {//input validation because test runs at 03:00 mean i'm gonna screw something up
				System.out.print("Input the ID of the essay in place " + (x+1) + ": ");
				id = input.nextInt();
				if(id <= 0 || id > config[1])
					System.out.println("Invalid ID, please try again.");
			}
			
			scores[id-1] = (x+1);//assign the correct place to the essay identified in the input
		}
		return scores;
	}
	
	private static int[][] scoreRanks(int[] ranks, int[][] essays) {//read ranks given by a single judge and update the essay data appropriately
		for(int x = 0; x < ranks.length; x++) {//iterate through every essay
			essays[x][6] = x+1;//tag essay with ID for sorting
			switch(ranks[x]) {
			case 1://this essay got first place
				essays[x][0]+=5;//increase its score by 5
				essays[x][1]++;//that this essay got one more first place finish
				break;
			case 2://this essay got first place
				essays[x][0]+=4;//increase its score by 4
				essays[x][2]++;//that this essay got one more second place finish
				break;
			case 3://this essay got first place
				essays[x][0]+=3;//increase its score by 3
				essays[x][3]++;//that this essay got one more third place finish
				break;
			case 4://this essay got first place
				essays[x][0]+=2;//increase its score by 2
				essays[x][4]++;//that this essay got one more fourth place finish
				break;
			case 5://this essay got first place
				essays[x][0]+=1;//increase its score by 1
				essays[x][5]++;//that this essay got one more fifth place finish
				break;
			}
		}
		return essays;
	}
	
	private static int[][] sortByCol(int col, int [][] arr, int start, int end){//sort sub-array of array 'arr' by column 'col'
		Arrays.sort(arr, new Comparator<int[]>() {
	           
	          @Override             
	          // Compare values according to column containing score
	          public int compare(final int[] entry1, final int[] entry2) {
	            if (entry1[col] < entry2[col])
	                return 1;
	            else
	                return -1;
	          }});
		
		return arr;
	}
	
	private static int[][] sortByCol(int col, int[][] arr){//overload sortByCol for defaulting to full array
		return sortByCol(col, arr, 0, (arr.length-1));
	}
	
	private static int[][] sortByCol(int[][] arr){//overload sortbyCol to default to first column
		return sortByCol(0, arr);
	}
	
	private static int[][] swapEssays(int[][] essays, int first, int second){//swaps two essays in essays array
		int[] temp = essays[first];//set aside first essay for swap
		essays[first]=essays[second];
		essays[second]=temp;
		return essays;
	}
	
	private static int[][] orderEssays(int[][] essays){//damn multilayered sort giving me a headache, will come back later
		essays=sortByCol(essays);//sort by scores
		for(int x = 0; x < (config[2]-1); x++) {//iterate through for all places and compare for tied scores for first round of tiebreaking
			if(essays[x][0]==essays[x+1][0]) {//if current iteration is tied with one ranked below it currently based on cumulative score
				essays=sortByCol(x, essays, x, x+1);//call sortByCol to sort tied pair by
				tiebreakerPlaces[x]++;//tag each place in tiebreaker as having had a tie broken
				tiebreakerPlaces[x+1]++;
			}
		}
		for(int x = 0; x < (config[2]-1); x++) {//iterate through for all places and compare for tied placements for second round of tiebreaking
			if(essays[x][0]==essays[x+1][0]) {//if current iteration is tied with one ranked below it currently based on number of placements in highest spot
				essays=sortByCol(x, essays, x, x+1);//call sortByCol to sort tied pair by
				tiebreakerPlaces[x]++;//tag each place in tiebreaker as having had a tie broken
				tiebreakerPlaces[x+1]++;
			}
		}
		return essays;
	}
	
	private static String[] formatResults(int[][] essays) {
		String[] results= new String[(config[2]+7)];//array of strings representing each line of the output file
		essays=orderEssays(essays);//run essay sorting logic
		results[0]="Read " + config[0] + " Judge file(s)";//message for judge file count
		results[2]="Placements:";//skip a line and add the placement header
		for(int x = 0; x < config[2]; x++) {//iterate through the placements, dynamically generate the 1st/2nd/3rd, etc
			results[3+x]=""+x;
			if(x==1)
				results[3+x].concat("st::");
			else if(x<3)
				results[3+x].concat("nd::");
			else if(x==3)
				results[3+x].concat("rd::");
			else
				results[3+x].concat("th::");//yes, I know that this won't get large placements quite right, but if you're seriously using this for 20+ essays and it bothers you, email me and let me know that the edge case happened
			if(essays[x][6]<10)//append the ID of the relevant essay, with a leading zero if it's a single digit value
				results[3+x].concat("0"+essays[x][6]); 
			else
				results[3+x].concat(""+essays[x][6]);
		}
		
		return results;
	}
	
	public static void main(String[] args) {
		Scanner cin = new Scanner(System.in);//temporary scanner for stdin
		//create integer array to hold the number of judges to expect input from, the number of essays, and the number of places to award
		getConfig(cin);
		int[][] essays = new int[config[1]][7];//array containing each essay's score and placement counts, as well as ID label for sorting, ordered by ID
		
		for(int x = 0; x < config[0];x++) {//read each judge's ballot
			//TODO insert file identification code here
			int[] judge = readBallot(cin);
			essays = scoreRanks(judge, essays);//update the essay data to reflect the scores from the ballot read by calling readBallot
		}
		
		for(String line : formatResults(essays))
			System.out.println(line);
	}

}
