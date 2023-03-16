import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
//        Stream<Course> cour = courses.stream();
        Map<String, Integer> res = new TreeMap<>();
        for (Course cour :
                courses) {
            if(res.containsKey(cour.institution))res.put(cour.institution,res.get(cour.institution) + cour.participants);
            else res.put(cour.institution,cour.participants);
        }

        return res;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> map = new TreeMap<>();
        for (Course cour :
                courses) {
            String key = cour.institution + "-"+ cour.subject;
            if(map.containsKey(key))map.put(key, map.get(key) + cour.participants);
            else map.put(key, cour.participants);
        }
        // 添加键值对
        Map<String, Integer> res = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return res;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> res = new HashMap<>();
        int count=0;
        for (Course cour :
                courses) {
            String[] instructors = cour.instructors.split(", ");
            //如果是independently responsible courses
            if(instructors.length == 1){
                if(res.containsKey(instructors[0])){
                    res.get(instructors[0]).get(0).add(cour.title);
                }else{
                    List<List<String>> courseList = new ArrayList<>();
                    courseList.add(new ArrayList<>());
                    courseList.add(new ArrayList<>());
                    courseList.get(0).add(cour.title);
                    res.put(instructors[0],courseList);
                }
            }else{
                for (String instructor :
                        instructors) {
                    if(res.containsKey(instructor)){
                        res.get(instructor).get(1).add(cour.title);
                    }else{
                        List<List<String>> courseList = new ArrayList<>();
                        courseList.add(new ArrayList<>());
                        courseList.add(new ArrayList<>());
                        courseList.get(1).add(cour.title);
                        res.put(instructor,courseList);
                    }
                }
            }
        }
        for (Map.Entry<String, List<List<String>>> entry : res.entrySet()) {
            List<List<String>> list = entry.getValue();
            List<String> list0 = list.get(0);
            List<String> list1 = list.get(1);
            Set<String> set0 = new LinkedHashSet<>(list0);
            List<String> newList0 = new ArrayList<>(set0);
            Set<String> set1 = new LinkedHashSet<>(list1);
            List<String> newList1 = new ArrayList<>(set1);
            Collections.sort(newList0);
            Collections.sort(newList1);
            List<List<String>> newList = new ArrayList<>();
            newList.add(newList0);
            newList.add(newList1);
            res.put(entry.getKey(),newList);
        }

        return res;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        Map<Course, Double> map = new HashMap<>();
        if(by.equals("hours")){
            for (Course cour :
                    courses) {
                map.put(cour,cour.totalHours);
            }
        }
        if(by.equals("participants")){
            for (Course cour :
                    courses) {
                map.put(cour, (double)cour.participants);
            }
        }
        Map<Course, Double> sortedMap = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        int i=0;
        List<String> res = new ArrayList<>();
        for(Map.Entry<Course, Double> entry:
                sortedMap.entrySet()){
            if(i == topK)break;
            else{
                if(res.contains(entry.getKey().title))continue;
                else{
                    res.add(entry.getKey().title);
                    i++;
                }

            }

        }

        return res;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        return null;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        return null;
    }

}

class Q2Comparator implements Comparator<Map.Entry<String, Integer>> {
    @Override
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        return o1.getValue() - o2.getValue();
    }
}
class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }
}