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


    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> res = new TreeMap<>();
        for (Course cour :
                courses) {
            if(res.containsKey(cour.institution))res.put(cour.institution,res.get(cour.institution) + cour.participants);
            else res.put(cour.institution,cour.participants);
        }

        return res;
    }


    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> map = new TreeMap<>();
        for (Course cour :
                courses) {
            String key = cour.institution + "-"+ cour.subject;
            if(map.containsKey(key))map.put(key, map.get(key) + cour.participants);
            else map.put(key, cour.participants);
        }

        Map<String, Integer> res = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return res;
    }


    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> res = new HashMap<>();
        int count=0;
        for (Course cour :
                courses) {
            String[] instructors = cour.instructors.split(", ");

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


    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        List<String> res = new ArrayList<>();
        for (Course cour :
                courses) {
            if (cour.subject.toLowerCase().contains(courseSubject.toLowerCase()) && cour.percentAudited >= percentAudited && cour.totalHours <= totalCourseHours)
                res.add(cour.title);
        }
        Set<String> set = new HashSet<String>(res);
        res = new ArrayList<String>(set);
        Collections.sort(res);
        return res;
    }


    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {

        Map<String, CourseByNumber> map = new HashMap<>();
        for (Course cour :
                courses) {
           if(map.containsKey(cour.number))
              map.get(cour.number).courses.add(cour);
           else{
               CourseByNumber addCourse = new CourseByNumber();
               List<Course> add = new ArrayList<>();
               add.add(cour);
               addCourse.courses = add;
               map.put(cour.number, addCourse);
           }
        }


        for (Map.Entry<String, CourseByNumber> entry :
                map.entrySet()) {
            CourseByNumber cbn = entry.getValue();
            Collections.sort(cbn.courses, new Comparator<Course>() {
                @Override
                public int compare(Course o1, Course o2) {
                    return o1.launchDate.compareTo(o2.launchDate);
                }
            }.reversed());
            cbn.averAge = cbn.courses.stream()
                    .mapToDouble(e->e.medianAge)
                    .average()
                    .orElse(0.0);
            cbn.averPerMale = cbn.courses.stream()
                    .mapToDouble(e -> e.percentMale)
                    .average()
                    .orElse(0.0);
            cbn.averBachelorOrHigher = cbn.courses.stream()
                    .mapToDouble(e -> e.percentDegree)
                    .average()
                    .orElse(0.0);
            cbn.similarityVal = Math.pow(age - cbn.averAge, 2) + Math.pow(gender * 100 - cbn.averPerMale, 2) + Math.pow(isBachelorOrHigher * 100 - cbn.averBachelorOrHigher, 2);
        }

        Map<String, CourseByNumber> sortedMap = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> courseTitles = new ArrayList<>();
        int i=0;
        for (Map.Entry<String, CourseByNumber> entry:
        sortedMap.entrySet()){
            if(i>=10)break;
            if(!courseTitles.contains(entry.getValue().courses.get(0).title)){
                courseTitles.add(entry.getValue().courses.get(0).title);
                i++;
            }
        }
        return courseTitles;
    }

}

class Q2Comparator implements Comparator<Map.Entry<String, Integer>> {
    @Override
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        return o1.getValue() - o2.getValue();
    }
}

class CourseByNumber implements Comparable<CourseByNumber>{
    String courseNumber;
    List<Course> courses;
    double averAge;
    double averPerMale;
    double averBachelorOrHigher;
    double similarityVal;

    @Override
    public String toString() {
        return "CourseByNumber{" +
                "courseNumber='" + courseNumber + '\'' +
                ", courses=" + courses +
                ", averAge=" + averAge +
                ", averPerMale=" + averPerMale +
                ", averBachelorOrHigher=" + averBachelorOrHigher +
                ", similarityVal=" + similarityVal +
                '}';
    }

    @Override
    public int compareTo(CourseByNumber o) {
        int flag;
        if(this.similarityVal - o.similarityVal > 0)flag = 1;
        else if (this.similarityVal - o.similarityVal<0)flag = -1;
        else{
            if(this.courses.get(0).title.compareTo(o.courses.get(0).title) > 0) flag =1;
            else flag =-1;
        }
        return flag;
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

    @Override
    public String toString() {
        return "Course{" +
                "number='" + number + '\'' +
                ", launchDate=" + launchDate +
                ", title='" + title + '\'' +
                '}';
    }

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