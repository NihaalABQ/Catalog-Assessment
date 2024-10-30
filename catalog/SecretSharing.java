import org.json.JSONObject;
import org.json.JSONTokener;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SecretSharing {
    public static void main(String[] args) throws Exception {
        // Load JSON from a file
        String jsonData = new String(Files.readAllBytes(Paths.get("C:/Users/nihaa/Downloads/sample1.json")));
        JSONObject jsonObject = new JSONObject(new JSONTokener(jsonData));

        // Extract n and k
        int n = jsonObject.getJSONObject("keys").getInt("n");
        int k = jsonObject.getJSONObject("keys").getInt("k");

        // Prepare to store points
        Map<Integer, BigInteger> points = new HashMap<>();

        // Read each point
        for (String key : jsonObject.keySet()) {
            if (key.equals("keys")) continue;
            int x = Integer.parseInt(key);
            JSONObject point = jsonObject.getJSONObject(key);
            int base = point.getInt("base");
            String value = point.getString("value");

            // Decode y value from specified base
            BigInteger y = new BigInteger(value, base);
            points.put(x, y);
        }

        // Calculate the constant term (c) using Lagrange interpolation
        BigInteger constantTerm = findConstantTerm(points, k);
        System.out.println("The constant term (secret) is: " + constantTerm);
    }

    private static BigInteger findConstantTerm(Map<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;

        // Implement Lagrange interpolation at x = 0
        for (Map.Entry<Integer, BigInteger> entry : points.entrySet()) {
            int xi = entry.getKey();
            BigInteger yi = entry.getValue();

            BigInteger term = yi;
            for (Map.Entry<Integer, BigInteger> other : points.entrySet()) {
                int xj = other.getKey();
                if (xj != xi) {
                    term = term.multiply(BigInteger.valueOf(-xj))
                               .divide(BigInteger.valueOf(xi - xj));
                }
            }
            result = result.add(term);
        }

        return result;
    }
}
