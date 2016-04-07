package controllers.Security;

import models.User;

import java.security.MessageDigest;

/**
 * Created by Stefan on 12.03.2015.
 * checks and hashes passwords
 */
public class PasswordChecker {

    /**
     * right now: simple java.lang.String.hashCode function s.t.c
     * @param user user from database
     * @param passwordToCheck self-explanatory
     * @return whether or not password is correct
     */
    public static boolean check(User user, String passwordToCheck) {
        return user.getPassword().equals(hash(passwordToCheck));
    }

    /**
     * creates md5 hash for given password
     * @param password to be hashed
     * @return hashed sequence as String
     */
    public static String hash(String password) {
        String  hash    = new String();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(password.getBytes());
            byte[] digested = messageDigest.digest();//creating hash

            for (byte aDigest : digested) {//writing hash in String
                String hex = Integer.toHexString(aDigest);
                hash += hex;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * creates a hash with old password hash and random numbers of length between 1-9
     * @param password old password hash
     * @return hash for link
     */
    public static String restoreHash(String password) {
        String  hash;
        double  spaceLength;//length of noise between hash of old password
        do { //creates spaceLength != 0
            spaceLength         = (int) (Math.random() * 10);
        }while (spaceLength == 0);// length = 0
        double  exponentiation  = Math.pow(10.0, spaceLength);
        int     oldHashLength   = password.length();
        int     random;
        hash                    = (int) spaceLength + oldHashLength + "/";//encrypts noseLength with oldHasLength so it cant be determined
        for(int i = 0; i < oldHashLength; i++) {
            do {
                random = (int) (Math.random() * exponentiation);
            }while(random == 0); //if random = 0 *10 does not work to enlarge it
            while(random / (exponentiation / 10) < 1) { //makes sure the random number has spaceLength number of digits
                random          = random * 10;
            }
            hash                = hash + random + password.charAt(i);//adds noise + 1 char of oldPasswordHash
        }
        return hash;
    }

    /**
     *
     * @param oldHash hash of old password
     * @param spaceLength spaceLength as oldHashLength + actual spaceLength
     * @param hashToCheck the hash to check
     * @return true if hash is valid
     */
    public static boolean checkRestoreHash(String oldHash, int spaceLength, String hashToCheck) {
        int                     oldHashLength       = oldHash.length();
        int                     actualSpaceLength   = spaceLength - oldHashLength; //decrypts noiseLength
        String                  hashedHash          = "";//where the hash without noise is stored
        int                     index               = 0;
        for(int i = 0; i < hashToCheck.length(); i++) {
            if(index == actualSpaceLength) {//checks if position in hash is a relevant char or a noise char by comparing relative(from last relevant char) position and noiseLength
                hashedHash = hashedHash + hashToCheck.charAt(i);//adds relevant char to checkHash
                index = 0;
            } else {
                index++;//increases distance to last relevant position
            }
        }
        return oldHash.equals(hashedHash);//checks if given hash is hash of old password and returns result
    }

}
