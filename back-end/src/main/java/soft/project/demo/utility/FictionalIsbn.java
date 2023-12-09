package soft.project.demo.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.data.util.Pair;

import soft.project.demo.exception.InvalidIsbnException;
import soft.project.demo.model.Book;

public class FictionalIsbn {
	
	private static final Map<String, List<Pair<String, String>>> mapWithPairs = new HashMap<>();
	
	public static void addValueToMap(String key, String value1, String value2) {
        // Synchronize access if needed
        synchronized (mapWithPairs) {
            List<Pair<String, String>> pairList = mapWithPairs.computeIfAbsent(key, k -> new ArrayList<>());
            pairList.add(Pair.of(value1, value2));
        }
    }
	
	static {
		addValueToMap("1 digit", "0-6398000", "0-6399999");
		addValueToMap("1 digit", "0-6450000", "0-6459999");
		addValueToMap("1 digit", "0-6480000", "0-6489999");
		addValueToMap("1 digit", "0-9003710", "0-9003719");
		addValueToMap("1 digit", "0-9500000", "0-9999999");
		
		addValueToMap("1 digit", "1-0670000", "1-0699999");
		addValueToMap("1 digit", "1-7320000", "1-7399999");
		addValueToMap("1 digit", "1-7635000", "1-7649999");
		addValueToMap("1 digit", "1-7750000", "1-7753999");
		addValueToMap("1 digit", "1-7764000", "1-7764999");
		addValueToMap("1 digit", "1-7770000", "1-7782999");
		addValueToMap("1 digit", "1-8380000", "1-8384999");
		addValueToMap("1 digit", "1-9160000", "1-9165059");
		addValueToMap("1 digit", "1-9168700", "1-9169079");
		addValueToMap("1 digit", "1-9196000", "1-9196549");
		addValueToMap("1 digit", "1-9911500", "1-9911999");
		addValueToMap("1 digit", "1-9989900", "1-9999999");
		
		addValueToMap("2 digits", "0-900000", "0-900370");
		addValueToMap("2 digits", "0-900372", "0-949999");
		
		addValueToMap("2 digits", "1-869800", "1-915999");
		addValueToMap("2 digits", "1-916506", "1-916869");
		addValueToMap("2 digits", "1-916908", "1-919599");
		addValueToMap("2 digits", "1-919655", "1-972999");
		addValueToMap("2 digits", "1-987800", "1-991149");
		addValueToMap("2 digits", "1-991200", "1-998989");
		
		addValueToMap("3 digits", "0-85000", "0-89999");
		
		addValueToMap("3 digits", "1-55000", "1-64999");
		addValueToMap("3 digits", "1-68000", "1-68599");
		addValueToMap("3 digits", "1-74000", "1-76199");
		addValueToMap("3 digits", "1-76500", "1-77499");
		addValueToMap("3 digits", "1-77540", "1-77639");
		addValueToMap("3 digits", "1-77650", "1-77699");
		addValueToMap("3 digits", "1-77830", "1-78999");
		addValueToMap("3 digits", "1-80000", "1-83799");
		addValueToMap("3 digits", "1-83850", "1-86719");
		addValueToMap("3 digits", "1-86760", "1-86979");
		addValueToMap("4 digits", "0-2280", "0-2289");
		addValueToMap("4 digits", "0-3690", "0-3699");
		addValueToMap("4 digits", "0-6390", "0-6397");
		addValueToMap("4 digits", "0-6550", "0-6559");
		addValueToMap("4 digits", "0-7000", "0-8499");
		
		addValueToMap("4 digits", "1-0350", "1-0399");
		addValueToMap("4 digits", "1-0700", "1-0999");
		addValueToMap("4 digits", "1-3980", "1-5499");
		addValueToMap("4 digits", "1-6500", "1-6799");
		addValueToMap("4 digits", "1-6860", "1-7139");
		addValueToMap("4 digits", "1-7170", "1-7319");
		addValueToMap("4 digits", "1-7620", "1-7634");
		addValueToMap("4 digits", "1-7900", "1-7999");
		addValueToMap("4 digits", "1-8672", "1-8675");
		addValueToMap("4 digits", "1-9730", "1-9877");
		
		addValueToMap("5 digits", "0-200", "0-227");
		addValueToMap("5 digits", "0-229", "0-368");
		addValueToMap("5 digits", "0-370", "0-638");
		addValueToMap("5 digits", "0-640", "0-644");
		addValueToMap("5 digits", "0-646", "0-647");
		addValueToMap("5 digits", "0-649", "0-654");
		addValueToMap("5 digits", "0-656", "0-699");
		
		addValueToMap("5 digits", "1-000", "1-009");
		addValueToMap("5 digits", "1-030", "1-034");
		addValueToMap("5 digits", "1-040", "1-049");
		addValueToMap("5 digits", "1-100", "1-397");
		addValueToMap("5 digits", "1-714", "1-716");
		
		addValueToMap("6 digits", "0-00", "0-19");
		
		addValueToMap("6 digits", "1-01", "1-02");
		addValueToMap("6 digits", "1-05", "1-05");
	}
	
	private static Pair<String, String> getRandomPairForKey(String key) {
		Random random = new Random();
		
        List<Pair<String, String>> pairsForKey = mapWithPairs.get(key);
        
        int zeros = 0, ones = 0, largerNum = 0, sample = 0, getSample = 0, getIndex = 0;

        if (pairsForKey != null && !pairsForKey.isEmpty()) {
           // Collections.shuffle(pairsForKey);
        	for(Pair<String, String> pair : pairsForKey) {
        		if(Integer.parseInt(pair.getFirst().substring(0,1)) == 0) {
        			zeros++;
        		}
        		else if (Integer.parseInt(pair.getFirst().substring(0,1)) == 1) {
        			ones++;
        		}
        	}
        	if(ones >= zeros) {
        		largerNum = ones;	
        	}
        	else if (ones < zeros){
        		largerNum = zeros;
        	}
        	
        	sample = random.nextInt(2*largerNum)+1;
    		if(sample <= largerNum) {
    			getSample = zeros;
    			getIndex = random.nextInt(getSample);
    		}
    		else {
    			getSample = ones;
    			getIndex = zeros + random.nextInt(getSample);
    		}
        	
            return pairsForKey.get(getIndex);
        } else {
            return null;
        }
    }
	
	private static Pair<String, String> getExactPairForKey(String key, String regPart){
		List<Pair<String, String>> pairsForKey = mapWithPairs.get(key);
		
		String regGroup = regPart.substring(0, 1);
		
		String registrant = regPart.substring(2);
		
		if (pairsForKey != null && !pairsForKey.isEmpty()) {
			for(Pair<String, String> pair : pairsForKey) {
				if(Integer.parseInt(pair.getFirst().substring(0,1)) == 
				   Integer.parseInt(regGroup) &&
				   Integer.parseInt(pair.getFirst().substring(2)) <=
						   Integer.parseInt(registrant) &&
				   Integer.parseInt(pair.getSecond().substring(2)) >=
						   Integer.parseInt(registrant)
				  )
				  return pair;
        	}
		}
		return null;
	}
	
	public static String getClosePossibleIsbn(String isbn, List<Book> books) {
		Random random = new Random();
		StringBuilder isbnBuilder = new StringBuilder();
		StringBuilder nines = new StringBuilder("9");
		int randNum = -1;
		int hyphen1 = -1, hyphen2 = -1, hyphen3 = -1, hyphen4 = -1;
		int registrant = -1, publication = -1;
		int prefix = -1;
		int checkDigit = -1, digit = -1;
		int newRegistrant = -1, newPublication = -1, pubRange = -1;
		int checkReg = -1, checkPub = -1;
		char digitChar = '\u0000';
    	int product = 0;
		String regGroupStr = "";
		String registrantStr = "";
		String regPart = "";
		String publicationStr = "";
		String theKey = "";
		int pubPartDigitsNum = -1;
		boolean loop = false;
        int iterations = 0, maxIterations = 50000;
		
		if(isValidIsbn(isbn, books)) {
			hyphen1 = isbn.indexOf('-');
			hyphen2 = isbn.indexOf('-', hyphen1 + 1);
			hyphen3 = isbn.indexOf('-', hyphen2 + 1);
			hyphen4 = isbn.indexOf('-', hyphen3 + 1);
			
			prefix = Integer.parseInt(isbn.substring(0, hyphen1));;
			regGroupStr = isbn.substring(hyphen1+1, hyphen2);
			registrantStr = isbn.substring(hyphen2+1, hyphen3);
			registrant = Integer.parseInt(registrantStr);
			newRegistrant = registrant;
			regPart = regGroupStr+'-'+registrantStr;
			publicationStr = isbn.substring(hyphen3+1, hyphen4);
			publication = Integer.parseInt(publicationStr);
			newPublication = publication;
			
			pubPartDigitsNum = publicationStr.length();
			
			theKey = Integer.toString(pubPartDigitsNum) + (pubPartDigitsNum > 1 ? " digits" : " digit");
			
			Pair<String, String> exactPair = getExactPairForKey(theKey, regPart);
			
			for (int i = 1; i < pubPartDigitsNum; i++) {
	            nines.append("9");
	        }
	        
	        pubRange = Integer.parseInt(nines.toString());
			
			do {
				if(registrant == Integer.parseInt(exactPair.getFirst().substring(2))) {
					newRegistrant++;
				} else
				if(registrant == Integer.parseInt(exactPair.getSecond().substring(2))) {
					newRegistrant--;
				}
				else 
				if(registrant > Integer.parseInt(exactPair.getFirst().substring(2)) &&
				   registrant < Integer.parseInt(exactPair.getSecond().substring(2))	
				  ) {
					randNum = random.nextInt(10);
					if(randNum % 2 == 0) newRegistrant++;
					else newRegistrant--;
				}
				
				if(newPublication == checkPub) {
					if(newPublication == pubRange) {
						newPublication--;
					}
					else if(newPublication == 0) {
						newPublication++;
					}
					else if(newPublication < pubRange && newPublication > 0) {
						randNum = random.nextInt(10);
						if(randNum % 2 == 0) newPublication++;
						else newPublication--;
					}
				}
				
				loop = false;
				for(Book book : books) {
		        	isbn = book.getIsbn();
		        	
		        	hyphen2 = isbn.indexOf('-', isbn.indexOf('-') + 1);
		        	hyphen3 = isbn.indexOf('-', hyphen2 + 1);
		        	checkReg = Integer.parseInt(isbn.substring(hyphen2 + 1, hyphen3));
		        	
		        	hyphen4 = isbn.indexOf('-', hyphen3 + 1);
		        	checkPub = Integer.parseInt(isbn.substring(hyphen3 + 1, hyphen4)) ;
		            
		        	if(newRegistrant == checkReg && newPublication == checkPub) {
		        		loop = true;
		        		break;
		        	}
		        }
				
				iterations++;

		        if (iterations > maxIterations) {
		        	String str1 = "Exceeded maximum iterations. Adjust parameters to avoid infinite loop.";
		        	String str2 = "\nAll checked books have registrant and pubication element numbers already taken.";
		        	String str3 = "\nYou can check another possible registrant and publication ranges of an isbn.";
		        	String str = str1+str2+str3;
		            throw new RuntimeException(str);
		        }	
			}while(loop);
			
			String regFormat = "%0" + String.valueOf(8-pubPartDigitsNum) + "d";
	        String pubFormat = "%0" + String.valueOf(pubPartDigitsNum) + "d";
	        
	        isbnBuilder.append(prefix)
	        .append(regGroupStr)
	        .append(String.format(regFormat, newRegistrant))
	        .append(String.format(pubFormat, newPublication));
	        
	        product = 0;
	        for (int i = 0; i < isbnBuilder.length(); i++) {
	            digitChar = isbnBuilder.charAt(i);

	            digit = Character.getNumericValue(digitChar);

	            product = product + digit * (i % 2 == 0 ? 1 : 3);
	        }
	        
	        if(product%10 == 0) checkDigit = 0;
	        else checkDigit = 10 - product%10;
	        
	        isbnBuilder.setLength(0);
	        
	        isbnBuilder.append(prefix).append("-")
	        .append(regGroupStr).append("-")
	        .append(String.format(regFormat, newRegistrant)).append("-")
	        .append(String.format(pubFormat, newPublication)).append("-")
	        .append(checkDigit);
	        
	        return isbnBuilder.toString();
		}
		return null;
	}
	
	public static String makeUniqueFictionalIsbn(List<Book> books, int pubDigits) {
		Random random = new Random();
    	StringBuilder isbnBuilder = new StringBuilder();
    	StringBuilder nines = new StringBuilder("9");
    	String isbn = "";
    	int prefix = 978;
    	int regGroup = 1;
    	int registrant = 0;
    	int publication = 0;
    	int checkDigit = 0;
    	int digit = 0;
    	char digitChar = '\u0000';
    	int product = 0;
    	int minRange = 1;
        int regRange = 99999, pubRange = 99999;
        int checkReg = 0, checkPub = 0;
        boolean loop = false;
        int maxIterations = 50000;  // Set a reasonable maximum number of iterations
        int iterations = 0;  // Counter to track the number of iterations
        int hyphen2 = 0, hyphen3 = 0, hyphen4 = 0;
        
        if(pubDigits>6 || pubDigits<1) {
        	digit = random.nextInt(6 - 1 + 1) + 1;
        }
        else digit = pubDigits;
        
        isbn = digit > 1 ? " digits" : " digit";
        
        String selectedKey = String.valueOf(digit)+isbn;
        Pair<String, String> randomPair = getRandomPairForKey(selectedKey);
        
        for (int i = 1; i < digit; i++) {
            nines.append("9");
        }
        
        pubRange = Integer.parseInt(nines.toString());
        
        do {
        	loop = false;
        	regGroup = Integer.parseInt(randomPair.getSecond().substring(0, 1));
        	regRange = Integer.parseInt(randomPair.getSecond().substring(2));
        	minRange = Integer.parseInt(randomPair.getFirst().substring(2));
	        registrant = random.nextInt(regRange - minRange + 1) + minRange;
	        
	        minRange = 1;
	        publication = random.nextInt(pubRange - minRange + 1) + minRange;
	        
	        for(Book book : books) {
	        	isbn = book.getIsbn();
	        	
	        	hyphen2 = isbn.indexOf('-', isbn.indexOf('-') + 1);
	        	hyphen3 = isbn.indexOf('-', hyphen2 + 1);
	        	checkReg = Integer.parseInt(isbn.substring(hyphen2 + 1, hyphen3));
	        	
	        	hyphen4 = isbn.indexOf('-', hyphen3 + 1);
	        	checkPub = Integer.parseInt(isbn.substring(hyphen3 + 1, hyphen4)) ;
	            
	        	if(registrant == checkReg && publication == checkPub) {
	        		loop = true;
	        		break;
	        	}
	        }
	        
	        iterations++;

	        if (iterations > maxIterations) {
	        	String str1 = "Exceeded maximum iterations. Adjust parameters to avoid infinite loop.";
	        	String str2 = "\nAll checked books have registrant and pubication element numbers already taken.";
	        	String str = str1+str2;
	            throw new RuntimeException(str);
	        }
	        
        }while(loop);
        
        String regFormat = "%0" + String.valueOf(8-digit) + "d";
        String pubFormat = "%0" + String.valueOf(digit) + "d";
        
        isbnBuilder.append(prefix)
        .append(regGroup)
        .append(String.format(regFormat, registrant))
        .append(String.format(pubFormat, publication));
        
        for (int i = 0; i < isbnBuilder.length(); i++) {
            digitChar = isbnBuilder.charAt(i);

            digit = Character.getNumericValue(digitChar);

            product = product + digit * (i % 2 == 0 ? 1 : 3);
        }
        
        if(product%10 == 0) checkDigit = 0;
        else checkDigit = 10 - product%10;
        
        isbnBuilder.setLength(0);
        
        isbnBuilder.append(prefix).append("-")
        .append(regGroup).append("-")
        .append(String.format(regFormat, registrant)).append("-")
        .append(String.format(pubFormat, publication)).append("-")
        .append(checkDigit);
        
        isbn = isbnBuilder.toString();
        
    	return isbn;
    }
	
	public static boolean isValidIsbn(String isbn, List<Book> books) {
		int prefix = 978;
    	int regGroup = 1;
    	int registrant = 0, regLength = 0;
    	int publication = 0, pubLength = 0;
    	int checkDigit = 0, digit = 0;
    	int hyphen1 = -1, hyphen2 = -1, hyphen3 = -1, hyphen4 = -1;
    	char digitChar = '\u0000';
    	int product = 0;
    	int checkReg = 0, checkPub = 0;
    	
    	StringBuilder isbnBuilder = new StringBuilder();
		
		if(isbn.length() != 17) {
			throw new InvalidIsbnException("ISBN-13 length is invalid");
		}
		
		hyphen1 = isbn.indexOf('-');
		hyphen2 = isbn.indexOf('-', hyphen1 + 1);
		hyphen3 = isbn.indexOf('-', hyphen2 + 1);
		hyphen4 = isbn.indexOf('-', hyphen3 + 1);
		
		if(hyphen1 == -1 || hyphen2 == -1 || hyphen3 == -1 || hyphen4 == -1) {
			throw new InvalidIsbnException("Missing hyphens in the provided ISBN-13 string");
		}
		
		prefix = Integer.parseInt(isbn.substring(0, hyphen1));
		
		if(!(prefix == 978 || prefix == 979)) {
			throw new InvalidIsbnException("ISBN-13 string EAN prefix is invalid");
		}
		
		regGroup = Integer.parseInt(isbn.substring(hyphen1+1, hyphen2));
		
		if(!(regGroup == 0 || regGroup == 1)) {
			throw new InvalidIsbnException("ISBN-13 string registration group element is invalid");
		}
		
		registrant = Integer.parseInt(isbn.substring(hyphen2+1, hyphen3));
		
		regLength = isbn.substring(hyphen2+1, hyphen3).length();
		
		if(regLength < 2 || regLength > 7) {
			throw new InvalidIsbnException("ISBN-13 string registrant element number of digits is invalid");
		}
		
		publication = Integer.parseInt(isbn.substring(hyphen3+1, hyphen4));
		
		pubLength = isbn.substring(hyphen3+1, hyphen4).length();
		
		if(pubLength < 1 || pubLength > 6) {
			throw new InvalidIsbnException("ISBN-13 string publication element number of digits is invalid");
		}
		
		String selectedKey = String.valueOf(pubLength)+(pubLength > 1 ? " digits" : " digit");
		List<Pair<String, String>> pairsForKey = mapWithPairs.get(selectedKey);
		boolean registrantInRange = false;
		for(Pair<String, String> pair : pairsForKey) {
			if((registrant >= Integer.parseInt(pair.getFirst().substring(2)) &&
					registrant <= Integer.parseInt(pair.getSecond().substring(2))) &&
					regGroup == Integer.parseInt(pair.getFirst().substring(0, 1))) {
				registrantInRange = true;
				break;
			}
    	}
		
		if(!registrantInRange) {
			//return false;
			throw new InvalidIsbnException("ISBN-13 string registrant element is invalid");
		}
		
		checkDigit = Integer.parseInt(isbn.substring(hyphen4+1));
		
		if(checkDigit < 0 || checkDigit > 9) {
			throw new InvalidIsbnException("ISBN-13 string check digit value is invalid");
		}
		
		String regFormat = "%0" + String.valueOf(regLength) + "d";
        String pubFormat = "%0" + String.valueOf(pubLength) + "d";
        
        isbnBuilder.append(prefix)
        .append(regGroup)
        .append(String.format(regFormat, registrant))
        .append(String.format(pubFormat, publication))
        .append(checkDigit);
        
        for (int i = 0; i < isbnBuilder.length(); i++) {
            digitChar = isbnBuilder.charAt(i);

            digit = Character.getNumericValue(digitChar);

            product = product + digit * (i % 2 == 0 ? 1 : 3);
        }
        
        if(product % 10 != 0) {
        	throw new InvalidIsbnException("ISBN-13 string check digit is invalid");
        }
        
        for(Book book : books) {
        	isbn = book.getIsbn();
        	
        	hyphen2 = isbn.indexOf('-', isbn.indexOf('-') + 1);
        	hyphen3 = isbn.indexOf('-', hyphen2 + 1);
        	checkReg = Integer.parseInt(isbn.substring(hyphen2 + 1, hyphen3));
        	
        	hyphen4 = isbn.indexOf('-', hyphen3 + 1);
        	checkPub = Integer.parseInt(isbn.substring(hyphen3 + 1, hyphen4)) ;
            
        	if(registrant == checkReg && publication == checkPub) {
        		throw new InvalidIsbnException("ISBN-13 you provided is already used");
        	}
        }
		
		return true;
    }
}
