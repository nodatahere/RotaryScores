# RotaryScores
Simple score compilation program for a scholarship essay contest.

Input: A set of .txt files representing the scores provided by a set number of judges for a set number of essays, as well as a .txt file specifying the number of essays and judges to input results for.

Example Results file for best 5 of 10:
```
Filename: Judge<Judge ID here>.txt ex: Judge01.txt

Placement::Essay ID
1::02
2::09
3::05
4::06
5::04
```

Output: A single .txt file representing the ranking of each essay in the final results, identified by an ID number, along with relevant information for validating the results.

Example of an output file for the above input:

```
Filename: Output.txt

Read 1 Judge file

Placements:

1st: 02
2nd: 09
3rd: 05
4th: 06
5th: 04

There were no cumulative score ties, so the placement counts were not referenced.

The placement counts were not referenced, so a manual runoff is not needed. 
```

Ranking process: Each essay will be given a score based on the placement assigned it by each judge. The scores are then totaled, and used for a preliminary ranking, with a higher score being a better rank. In the event of a tie, the number of times each essay was placed in a given location is counted, and the ties are broken on that basis. In the event of a tie after weighting each based on their placement counts, a manual runoff is called for in the output file, and any judges who ranked the essays which are being manually considered in the place for which they are contending will be highlighted for removal from the pool.
