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
    static class History{
        double sc; //score
        int sk; //no.of skips
        History(double s, int k){
            sc=s;
            sk=k;
        }
    }

    static class MentorResult {
        String mID, na;
        double FIS; //FIS for final score
        int rank, ev_sk; //evaluation skipped
        MentorResult(String m, String n, double e) {
            mID = m;
            na = n;
            FIS = e;
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
    static Map<String, History> parseEvaluation(String filename){
        Map<String, History> map = new LinkedHashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String en = br.readLine();
            while((en = br.readLine()) != null) {
                String[] p = en.split(",", -1);
                if(p.length < 6)
                    continue;
                map.put(p[0].trim(), new History(Double.parseDouble(p[2].trim()), Integer.parseInt(p[5].trim())));
            }
        } catch (IOException e) {
            //ignoring this function if data is not available
        }
        return map;
    }
    static void writeCSV1(List<MentorResult> res, String filename) throws IOException {
        try(PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("Mentor_ID,Name,Final Score,Rank");
            for(MentorResult r : res){
                pw.printf("%s,%s,%.6f,%d%n", r.mID, r.na, r.FIS, r.rank);
            }
        }
    }
    static void writeCSV2(List<MentorResult> res, String filename) throws IOException {
        try(PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("Mentor_ID,Name,Final Score,Rank,No. of Evaluation Skipped");
            for(MentorResult r : res){
                pw.printf("%s,%s,%.6f,%d,%d%n", r.mID, r.na, r.FIS, r.rank, r.ev_sk);
            }
        }
    }
    public static void main(String[] args) throws IOException {
        List<Mentor> mentors = parseMentors("mentors.csv");
        List<Student> allStudents = parseStudents("students.csv");
        List<Interaction> allInteract = parseInteraction("interactions.csv");
        Map<String, History> evalMap = parseEvaluation("mentor_previousScores.csv");
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
            if(evalMap.containsKey(m.ment_id)) {
                History M_pre =evalMap.get(m.ment_id);
                if(M_pre.sk < 2 && M_c != 0.0)
                    M_f = updateScore(M_pre.sc, M_c);
                else if(M_pre.sk < 2 && M_c == 0.0)
                    M_f = applyDecay(M_c, 2);
                else
                    M_f = applyDecay(M_c, M_pre.sk+1);
            }
            results.add(new MentorResult(m.ment_id, m.name, M_f));
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
            if(results.get(i).FIS == 0.0)
                results.get(i).ev_sk+=1;
        }
        writeCSV1(results, "mentor_scores.csv"); //output file
        writeCSV2(results, "mentor_previousScores.csv"); //to store the data
        System.out.println("mentor_scores.csv written successfully.");
    }
}
