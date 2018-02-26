package crypto;

import java.util.Scanner;
/**
 *
 * @author Brian
 */
public class BCryptGen 
{
    /*
        This class is used for hashing your password so you can create
        your account via MySQL Query Browser.
    */
    public static void main(String[] args)
    {
        // Creates a Scanner object used to get keyboard input
        Scanner input = new Scanner(System.in);
        
        // Receives keyboard input for password
        System.out.println("Enter your password: ");
        String password = input.nextLine();
        
        
        // Generates the salt, hashes the password, and provides the user with the hashed password
        String salt = BCrypt.gensalt();
        System.out.println("Your hashed password is: " + BCrypt.hashpw(password, salt));
        input.close();
        
    }
    
}
