import java.io.*;
import java.util.*;
import java.util.stream.*;
public class Scoring_System {
    static double w1 = 0.30, w2 = 0.30, w3 = 0.30, w4 = 0.10, a = 0.60, d = 0.15;

    static class Mentor {
        String ment_id, name, dom; // dom for domain, ment_id for mentor ID
        List<String> projects;

        Mentor(String m, String n, String d, List<String> p) {
            ment_id = m;
            name = n;
            dom = d;
            projects = p;
        }
    }
    static class Student {
        String stud_id, nam, proj_id; //stud_id for student ID, nam for name, proj_id for Project ID
        int compms, totms; // compms for completed milestones, totms for total milestonesdouble fr; //fr for Feedback Rating, ranging from 1-5

        Student(String s, String n, String p, int a, int b) {
            stud_id = s;
            nam = n;
            proj_id = p;
            compms = a;
            totms = b;
        }
    }

    static class Interaction {
        String ment_ID, stud_ID;
        int me, cr, ms; // me for number of meetings, cr for number of code reviews, ms for message
        double avg_rt; // avg_rt for average response time

        Interaction(String m, String s, int a, int b, int c, double d) {
            ment_ID = m;
            stud_ID = s;
            me = a;
            cr = b;
            ms = c;
            avg_rt = d;

        }
    }

    static class MentorResult {
        String mID, na;
        double PS, RS, ES, FES, FIS; //PS for Progress Score, RS for Responsive Score, ES for engagement score, FES for feedback score
        int rank;

        MentorResult(String m, String n, double a, double b, double c, double d, double e) {
            mID = m;
            na = n;
            PS = a;
            RS = b;
            ES = c;
            FES = d;
            FIS = e; //FIS for final score
        }
    }

    static double studentsPS(List<Student> s) {
        if (s.isEmpty())
            return 0.0;
        double twc = 0.0, twt = 0.0, wc = 0.0, wt = 0.0;
        for (Student a : s) {
            if (a.totms == 0)
                continue;
            for (int i = 1; i <= a.totms; i++) {
                wt += (double) ((2.0 * i) / ((a.totms + 1) * a.totms));
                if (i == a.compms)
                    wc = wt;
            }
            twc += wc;
            twt += wt;
            wc = 0.0;
            wt = 0.0;
        }
        return (twc / twt);
    }

    static double responsivenessScore(double t) {
        double n = Math.log(4.0) / 12.0;
        double r = 0.0;
        if(t== -9999)
            return 0.0;
        r = Math.exp(-n * t);
        return r;
    }

    static double engagementScore(List<Interaction> i) {
        double t = 0.0, in, avg, es = 0.0; //t is for total interaction and in for interaction per mentee avg for average interaction es the engagementScore of the mentor
        if (i.isEmpty())
            return 0.0;
        for (Interaction a : i) {
            in = 0.30 * a.me + 0.60 * a.cr + 0.10 * a.ms;
            t += in;
        }
        avg = t / i.size();
        es = 2 * ((1.0 / (1.0 + Math.exp(-avg / 10.0))) - 0.5);
        return es;
    }

    static double menteeFBS(List<Double> rat, List<Student> m) {
        if (rat.isEmpty())
            return 0.0;
        double w, w1 = 0.0, w2 = 0.0, p, r;
        for (int i = 0; i < rat.size(); i++) {
            r = rat.get(i);
            if (r < 1 || r > 5)
                continue;
            p = 0.0;
            if (i < m.size()) {
                Student s = m.get(i);
                if (s.totms > 0)
                    p = (double) s.compms / s.totms;
            }
            if (p < 0.5) {
                w = 0.40 * r;
                w2 += 0.4;
            } else if (p < 1.0) {
                w = 0.80 * r;
                w2 += 0.80;
            } else {
                w = r;
                w2 += 1.0;
            }
            w1 += w;
        }
        double rate = w1 / w2;
        return (rate - 1.0) / 4.0;
    }

    static double updateScore(double ps, double cs) { //ps for previous score and cs for current score
        return (1 - a) * ps + a * cs;
    }

    static double applyDecay(double s, int t) { //s for score
        return s * Math.pow((1 - d), (double)t/2.0);
    }

    static List<Mentor> parseMentors(String filename) throws IOException {
        List<Mentor> l = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String ent = br.readLine(); // to skip the header
            while ((ent = br.readLine()) != null) {
                String[] p = ent.split(",", -1);
                if (p.length < 4)
                    continue;
                List<String> proj = new ArrayList<>();
                for (int i = 3; i < p.length; i++) {
                    proj.add(p[i].trim());
                }
                l.add(new Mentor(p[0].trim(), p[1].trim(), p[2].trim(), proj));
            }
        }
        return l;
    }

    static List<Student> parseStudents(String filename) throws IOException {
        List<Student> l = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String ent = br.readLine(); //skipping the header
            while((ent = br.readLine()) != null) {
                String[] p = ent.split(",", -1);
                if (p.length < 5)
                    continue;
                l.add(new Student(p[0].trim(), p[1].trim(), p[2].trim(), Integer.parseInt(p[3].trim()), Integer.parseInt(p[4].trim())));
            }
        }
        return l;
    }
    static Map<String, Double> parseFeedback(String filename) throws IOException {
        Map<String, Double> map = new LinkedHashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String ent = br.readLine(); //skipping the header
            while((ent = br.readLine()) != null) {
                String[] p = ent.split(",", -1);
                if (p.length < 3)
                    continue;
                String id = p[0].trim() + "_" + p[1].trim();
                double rat = Double.parseDouble(p[2].trim());
                map.put(id, rat);
            }
        }
        return map;
    }
    static List<Interaction> parseInteraction(String filename) throws IOException {
        List<Interaction> l = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String ent = br.readLine(); //skipping the header
            while((ent = br.readLine()) != null) {
                String[] p = ent.split(",", -1);
                if (p.length < 6)
                    continue;
                l.add(new Interaction(p[0].trim(), p[1].trim(), Integer.parseInt(p[2].trim()), Integer.parseInt(p[3].trim()), Integer.parseInt(p[4].trim()), Double.parseDouble(p[5].trim())));
            }
        }
        return l;
    }
    static Map<String, Double> parsePreviousScore(String filename){
        Map<String, Double> map = new LinkedHashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String en = br.readLine();
            while((en = br.readLine()) != null) {
                String[] p = en.split(",", -1);
                if(p.length < 2)
                    continue;
                String id = p[0].trim();
                double sc = Double.parseDouble(p[1].trim());
                map.put(id, sc);
            }
        } catch (IOException e) {
            //ignoring this function if data is not available
        }
        return map;
    }
    static Map<String, Integer> parseEvaluation(String filename){
        Map<String, Integer> map = new LinkedHashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String en = br.readLine();
            while((en = br.readLine()) != null) {
                String[] p = en.split(",", -1);
                if(p.length < 2)
                    continue;
                String id = p[0].trim();
                int ev = Integer.parseInt(p[1].trim());
                map.put(id, ev);
            }
        } catch (IOException e) {
            //ignoring this function if data is not available
        }
        return map;
    }
    static void writeCSV(List<MentorResult> res, String filename) throws IOException {
        try(PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("Rank,Mentor_ID,Name,Progress Score,Responsiveness Score,Engagement Score,Feedback Score,Final Score");
            for(MentorResult r : res){
                pw.printf("%d,%s,%s,%.6f,%.6f,%.6f,%.6f,%.6f%n", r.rank, r.mID, r.na, r.PS, r.RS, r.ES, r.FES, r.FIS);
            }
        }
    }
    public static void main(String[] args) throws IOException {
        List<Mentor> mentors = parseMentors("mentors.csv");
        List<Student> allStudents = parseStudents("students.csv");
        List<Interaction> allInteract = parseInteraction("interactions.csv");
        Map<String, Double> prevScores = parsePreviousScore("mentor_previousScores.csv");
        Map<String, Integer> evalMap = parseEvaluation("mentor_eva.csv");
        Map<String,Double> feedbackMap = parseFeedback("feedbacks.csv");
        Map<String, Student> studentMap = new LinkedHashMap<>();
        for(Student s : allStudents){
            studentMap.put(s.stud_id, s);
        }
        Map<String, List<Interaction>> interactByMentor = new LinkedHashMap<>();
        for(Interaction ia : allInteract) {
            interactByMentor
                    .computeIfAbsent(ia.ment_ID, k -> new ArrayList<>())
                    .add(ia);
        }
        Map<String, List<Student>> studentsByMentor = new LinkedHashMap<>();
        for(Map.Entry<String, List<Interaction>> entry : interactByMentor.entrySet()) {
            String mentorId = entry.getKey();
            List<Student> ms = new ArrayList<>();
            for (Interaction ia : entry.getValue()){
                Student s = studentMap.get(ia.stud_ID);
                if(s != null)
                    ms.add(s);
            }
            studentsByMentor.put(mentorId, ms);
        }
        List<MentorResult> results = new ArrayList<>();
        for(Mentor m : mentors) {
            List<Interaction> mInteract = interactByMentor
                    .getOrDefault(m.ment_id, new ArrayList<>());
            List<Student> mStudents = studentsByMentor
                    .getOrDefault(m.ment_id, new ArrayList<>());
            double PS = studentsPS(mStudents);
            double tAvg = mInteract.stream()
                    .mapToDouble(ia -> ia.avg_rt)
                    .average()
                    .orElse(-9999);
            double RS = responsivenessScore(tAvg);
            double ES = engagementScore(mInteract);
            List<Double> ratings = new ArrayList<>();
            for(Interaction ia : mInteract) {
                String key = m.ment_id + "_" + ia.stud_ID;
                if(feedbackMap.containsKey(key))
                    ratings.add(feedbackMap.get(key));
            }
            double FES = menteeFBS(ratings, mStudents);
            double M_c = w1*PS + w2*RS + w3*ES + w4*FES;
            double M_f = M_c;
            if(prevScores.containsKey(m.ment_id)) {
                double M_pre = prevScores.get(m.ment_id);
                M_f = updateScore(M_pre, M_c);
            }
            if(evalMap.containsKey(m.ment_id)){
                int ev = evalMap.get(m.ment_id);
                if(ev >= 2) {
                    M_f = applyDecay(M_f, ev);
                }
            }
            results.add(new MentorResult(m.ment_id, m.name, PS, RS, ES, FES, M_f));
        }
        results.sort((a, b) -> Double.compare(b.FIS, a.FIS));
        for(int i=0; i < results.size(); i++) {
            if(i==0){
                results.get(i).rank =1;
            }
            else{
                double prevS = results.get(i-1).FIS;
                double currS = results.get(i).FIS;
                if(currS == prevS){
                    results.get(i).rank = results.get(i-1).rank;
                }
                else {
                    results.get(i).rank = i+1;
                }
            }
        }
        writeCSV(results, "mentor_scores.csv");
        System.out.println("mentor_scores.csv written successfully.");
    }
}
