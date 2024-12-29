package pl.app.shared;

public class PathVariableExtractor {
    /**
     * Extracts the segment of a URL following the portion matched by the given pattern.
     *
     * @param pattern the pattern representing the static part of the URL, with placeholders in curly braces (e.g., "{name}").
     * @param fullUrl the full URL to extract the segment from.
     * @return the extracted trailing path segment following the matched portion of the pattern.
     * Example:
     * <pre>
     * {@code
     * String pattern = "/api/v1/containers/{containerName}/objects";
     * String fullUrl = "/api/v1/containers/containerName/objects/subfolder/file.name";
     * String result = extractPathSegment(pattern, fullUrl); // "subfolder/file.name"
     * }
     * </pre>
     */
    public static String extractVariableAfterPath(String pattern, String fullUrl) {
        String regex = pattern + "/(.*)";
        regex = regex.replaceAll("\\{[^}]+\\}", "[^/]+");
        regex = "^" + regex + "$";
        java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = compiledPattern.matcher(fullUrl);
        if (!matcher.matches() || matcher.groupCount() < 1) {
            throw new IllegalArgumentException("Invalid key");
        }
        return matcher.group(1);
    }
}
