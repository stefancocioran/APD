# Tema 2 - APD <span style="font-size:small;">&copy; Cocioran Stefan, 331 CA</span> 

### Tema2 - Coordinator (main class)
* inserts input files in a document array
* divides the documents into fragments of fixed dimensions (creates Map Tasks), which will   
be assigned to Map workers
* when the <b>Map stage</b> is complete, collects all the partial results 
* the partial results are grouped by document's name and the Reduce Tasks are created   
and passed on to the Reduce Workers 
* when the <b>Reduce stage</b> is complete, the Reduce Tasks are sorted by rank in descending order
* write the results in the output file
  
### MapTask
* This class contains the data of a Map task:
  * document name
  * the beginning offset of the document fragment
  * fragment size

### MapWorker
This type of worker will perform an operation called <b>Map</b> for a file fragment,  
which will generate some partial results in the form of <b>{key, value}</b> pairs.  
Each worker handles multiple file fragments that are assigned evenly between workers.

Map operation steps for each file fragment: 
 * if it is the first fragment of the document, set the cursor at the very beginning
 * else, set the cursor one position before the given offset; if the character before the   
fragment's offset is not a delimiter it means that the word was split and the boolean   
variable <b>splitWord</b> is set as true  
 * if the word was split, read characters until it meets a delimiter (to get over useless characters)
 * read what is left from the fragment; if it encounters a delimiter it means that a complete word  
was found and it is added to the dictionary and the longest words list if it meets the requirements
 * when the entire length of the given fragment was read, check if the last read character is a delimiter;  
if it is, it means that the last word in the fragment is complete
 * if the last read character is <b>not</b> a delimiter it means that the last word in the fragment   
might have missing characters; if so, read characters until it meets a delimiter or EOF and when it   
encounters a delimiter or EOF, the complete word was found and added to the task's dictionary  
 * when the last word for the current fragment was found, add the results in a <Task, Result> type of map
 
### ReduceTask
* This class contains the data of a Reduce task:
  * document name
  * the list of results from the Map operation for that document

There are also <b> get </b> methods implemented to extract the results obtained following the  
reduction process (rank, the longest word length, the number of words of maximum length).

### ReduceWorker
This type of worker receives tasks and calculates the final results for each one: the rank of each file,  
as well as the maximum word length in a file and the number of occurrences of words with that length.
