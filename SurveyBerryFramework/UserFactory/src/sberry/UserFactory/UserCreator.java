package sberry.UserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import sberry.QuestionFactory.QuestionFactory;
import sberry.SDK.Question;
import sberry.SDK.Survey;
import sberry.SDK.User;

/**This class deals with creating a user object from the inputed .bry file  
 * @author Fouad Abou Harfouche
 *
 */
public class UserCreator {

	/**This method takes a .bry file and uses the getAllQuestions method to get an arraylist of question objects, which it then
	 * uses to create a user object   
	 * @param file The .bry File 
	 * @param email The user's email
	 * @param pass The user's password
	 * @return User user 
	 * @throws FileNotFoundException
	 */
	public User checkFile(String file, String email, String pass)
			throws FileNotFoundException {
		QuestionFactory qf = new QuestionFactory();
		Scanner scan = new Scanner(new File(file));
		String title = "";
		String type = "";
		String mails = "";
		String all = "";
		while (scan.hasNextLine())
			all = all + scan.nextLine();
		scan.close();
		String[] questions = all.split("\\\\");
		ArrayList<Question> qall = getAllQuestions(questions, qf);
		String uuid = UUID.randomUUID().toString();
		int a = -1;
		int b = -1;
		int c = -1;
		for (int i = 0; i < qall.size(); i++) {
			if (qall.get(i).getQtype() == 5) {
				mails = qall.get(i).getField();
				a = i;
			}
			if (qall.get(i).getQtype() == 6) {
				title = qall.get(i).getQuestion();
				b = i;
			}
			if (qall.get(i).getQtype() == 7) {
				type = qall.get(i).getQuestion();
				c = i;
			}
		}
		if (a != -1) {
			qall.remove(a);
			
		}
		if(b!=-1){
			qall.remove(b);
		}
		if(c!=-1){
			qall.remove(c);
		}

		Survey s = new Survey(qall, title, uuid, mails,type);
		User user = new User(email, pass, s);
		return user;
	}

	/**Private helper method which takes an array of question strings and uses a QuestionFactory object to 
	 * create and return a ArrayList of question objects
	 * @param questions Array of question strings 
	 * @param qf QuestionFactory object
	 * @return ArrayList qall of question objects
	 */
	private ArrayList<Question> getAllQuestions(String[] questions,
			QuestionFactory qf) {
		String qtype = "";
		ArrayList<Question> qall = new ArrayList<Question>();
		for (int i = 1; i < questions.length; i++) {
			qtype = questions[i].substring(0, questions[i].indexOf(':'));
			Question q = qf.getQuestion(qtype);
			if (q == null) {
				throw new IllegalArgumentException(
						"Please Insert a valid question type. \n"
								+ "You have an invalid question type: '"
								+ qtype + "'.At question number :" + i);
			} else if (q.getQtype() == 5) {
				q.setField(questions, i);
			} else if (q.getQtype() == 6||q.getQtype() == 7) {
				q.setQuestion(questions, i);
			} else {
				q.setQuestion(questions, i);
				q.setField(questions, i);
				if (qtype.contains("*")) {
					q.setOptional(false);
				} else {
					q.setOptional(true);
				}
				if (q.getQuestion().length() == 0)
					throw new IllegalArgumentException("There is no Question!");
			}

			qall.add(q);
		}
		return qall;
	}

}
