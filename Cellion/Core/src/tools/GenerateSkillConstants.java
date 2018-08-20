package tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import client.Jobs;
import client.SkillFactory;

public class GenerateSkillConstants {

    //
    public static void main(String[] args) {
        System.setProperty("wzpath", "wz");
        System.out.println("Loading skills...");
        SkillFactory.load();// This takes a year
        System.out.println("Loaded skills.. Creating files.");

        String lastJob = "";
        FileWriter fw = null;
        File f = new File("./skills/");
        f.mkdirs();
        try {
            List<String> pastSkills = new LinkedList<String>();
            for (Jobs job : Jobs.values()) {
                String jobName = StringUtil.makeEnumHumanReadable(job.name().replaceAll("\\d+.*", "")).replace(" ", "");
                List<Integer> skills = SkillFactory.getSkillsByJob(job.getId());
                if (!lastJob.equals(jobName) && fw != null) {
                    fw.write("}");
                    fw.close();
                    pastSkills.clear();
                    fw = new FileWriter(new File(f, jobName + ".java"));
                    fw.write("package constants.skills;\r\n\r\n");
                    fw.write("public class " + jobName + "{\r\n");
                } else if (fw == null) {
                    fw = new FileWriter(new File(f, jobName + ".java"));
                    fw.write("package constants.skills;\r\n\r\n");
                    fw.write("public class " + jobName + "{\r\n");
                }
                for (Integer skillid : skills) {
                    String skillName = SkillFactory.getSkillName(skillid).toUpperCase();
                    skillName = skillName.replaceAll("[^\\w\\d\\s]", "");// Remove all special characters
                    skillName = skillName.replace(" ", "_");// We can't have spaces
                    skillName = skillName.replace("__", "_");// ??
                    skillName = skillName.replace("6TH", "SIXTH");// "6th Party Tonight" skill
                    if (skillName.endsWith("_")) {
                        skillName = skillName.substring(0, skillName.length() - 1);
                    }
                    if (skillName.contains("(")) {
                        int ind = skillName.indexOf("(");
                        int indEnd = skillName.indexOf(")");
                        if (ind - 2 > 0) {
                            skillName = skillName.substring(0, ind - 1);// Some skills have (EXPLORER).
                        } else if (indEnd > 0) {
                            skillName = skillName.substring(indEnd + 2);// Kanna has two sklls that require this
                        }
                    }
                    if (skillName.length() > 0) {
                        int inc = 1;
                        while (pastSkills.contains(skillName)) {
                            if (skillName.contains("_" + (inc - 1))) {
                                skillName = skillName.substring(0, skillName.indexOf("_" + (inc - 1)));
                            }
                            skillName += "_" + inc;
                            inc++;
                        }
                        fw.write("	public static final int " + skillName + " = " + skillid + ";\r\n");
                        pastSkills.add(skillName);
                    }
                }
                lastJob = jobName;
                // System.out.println(jobName + ": " + skills.toString());
            }
            fw.write("}");
            fw.close();
            System.out.println("Finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
