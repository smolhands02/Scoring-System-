1. HOW TO RUN THE PROGRAM :
 Requirements : 
- Java JDK 8 or higher
- No external Libraries required - uses only the Java Standard Library 
- Place the following CSV files in the same folder as Scoring_System.java :
. mentors.csv
. students.csv
. interactions.csv
. feedbacks.csv

2. INPUT FILES
 - menter.csv
Information about mentors
. Column                           Description
. MentorID                         Unique mentor identifier
. Name                             Mentor name
. Domain                           Mentor expertise
. Projects                         List of projects IDs (comma separated)
- students.csv
   Information about mentees
  . Column                           Description
  . StudentID                        Unique student identifier
  . Name                             Student name
  . ProjectID                        Project the student is working on
  . MilestonesCompleted              Number of completed milestones
  . TotalMilestones                  Total milestones assigned
- interaction.csv             
   Mentor - Mentee interaction data  
  . Column                          Description
  . MentorID                        Unique mentor identifier
  . StudentID                       Unique student identifier
  . Meetings                        Number of meetings held
  . CodeReviews                     Number of code reviews conducted
  . Messages                        Number of discussion messages exchanged
  . AvgResponseTime                 Average mentor response time (hours)
- feedbacks.csv
  . Student feedback ratings for mentors by their mentees
  . Column                          Description
  . MentorID                        Unique mentor identifier
  . StudentID                       Unique student identifier
  . Rating                          Student rating for their mentor (1-5)
3. OUTPUT
- Output on the output terminal :
   mentor_scores.csv written successfully.
- Output File :
  . The output file is a csv file containing the ranked list of mentors sorted by Final Score in descending order.
  . name : mentor_scores.csv
  . Column                          Description
  . Rank                            Mentor rank
  . Mentor_ID                       Unique mentor identifier
  . Name                            Mentor name
  . Final Score                     Combined weighted final score
  . Rank                            Mentor rank
- File to store the data :
  name : mentor_previousScores.csv 
    . Column                          Description
  . Rank                            Mentor rank
  . Mentor_ID                       Unique mentor identifier
  . Name                            Mentor name
  . Final Score                     Combined weighted final score
  . Rank                            Mentor rank
  . No. of Evaluation Skipped       No. of evaluation skipped
4. Functions/Methods :
- (i) studentsPS() : 
- Function used to calculate the aggregate progress of a mentor's mentees.
- Weightage of later milestones are more as compare to early milestone as later milestones are harder.
- weightage of ith milestone is : i/(n(n+1)/2) , n = total number of milestones of a mentee
- (ii) responsivenessScore() :
- Measures how quickly the mentor responds to student queries.
- Designed to yeild higher scores for faster responses and very slow responses are heavily penalised
- The score is bounded in the range of (0,1]
- (iii) engagementScore():
- Measure the depth of mentor-mentee interaction, normalised per mentee.
- Code reviews weighted the most as it's the most engaging interaction which test both the mentee and mentor skills.
- Bounded in [0,1]
- (iv) menteeFBS() :
- Calculate the mentor's feedback (overall) rating on the bases of the mentee's rating and the mentee's own progress.
- Students who completed more milestones are considered more credible reviews.
- The score is bounded in the range of [0,1].
- (v) updateScore() :
- For multiplr evaluation periods
- Recent activity is weighted more heavily than past performance
- applyDecay() :
- Mentor who become inactive should gradually lose score, at a decay rate of 15%.
- (vii) writeCSV1() :
- It takes the final results list and save it as a .csv file on our computer.
-  (viii) writeCSV2() :
-  It takes the final results list and save it as a .csv file on our computer.
- (ix) parse methods :
- parseMentors()     : Reads mentor data.
- parseStudents()    : Reads student data.
- parseFeedback()    : Reads feedback rating.
- parseInteraction() : Reads interaction data.
- parseEvaluation()  : Reads the previous data.
5. User defined data type / class :
- Mentor : Represents one mentor with all their basic information
- Students : Represents one mentee with all their basic information
- Interaction : Represents one mentor-mentee interaction record.
- MentorResult : Stores the final computed scores for one mentor after all calculations are done.
- History : Stores the final score and no. of evaluation skipped for given Mentor_ID.
