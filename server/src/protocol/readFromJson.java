package protocol;

import com.google.gson.annotations.SerializedName;
/**
 * reading the jason file
 * @author Xps
 *
 */
public class readFromJson {
	
	@SerializedName("questions")
	public question [] qustions;
	/**
	 * creating the question object from the jasopn
	 * @author omribenm
	 *
	 */
	public class question{
		@SerializedName("questionText")
		public String questionText;
		@SerializedName("realAnswer")
		public String realAnswer;
	}
	
}