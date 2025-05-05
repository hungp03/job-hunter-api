package com.example.jobhunter.service;

import com.example.jobhunter.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.jobhunter.entity.Job;
import com.example.jobhunter.entity.Skill;
import com.example.jobhunter.entity.Subscriber;
import com.example.jobhunter.dto.response.email.ResEmailJob;
import com.example.jobhunter.repository.JobRepository;
import com.example.jobhunter.repository.SkillRepository;
import com.example.jobhunter.repository.SubscriberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public Subscriber findByEmail(String email){
        return this.subscriberRepository.findByEmail(email);
    }

    public boolean isEmailExisted(String email){
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber subscriber){
        if (isEmailExisted(subscriber.getEmail())){
            throw new IdInvalidException("Email đã được đăng ký!");
        }

        //check skill is valid
        if (subscriber.getSkills() != null){
            List<Long> reqSkills = subscriber.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber update(Subscriber request) {
        Subscriber subscriber = subscriberRepository.findById(request.getId())
                .orElseThrow(() -> new IdInvalidException("Subscriber id = " + request.getId() + " không tồn tại"));

        // Update fields if necessary
        if (request.getSkills() != null) {
            List<Long> skillIds = request.getSkills().stream()
                    .map(Skill::getId)
                    .toList();
            List<Skill> skills = skillRepository.findByIdIn(skillIds);
            subscriber.setSkills(skills);
        }

        return subscriberRepository.save(subscriber);
    }


    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (!listSubs.isEmpty()) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && !listSkills.isEmpty()) {

                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);

                    if (!listJobs.isEmpty()) {
                        List<ResEmailJob> arr = listJobs.stream().map(this::toResEmailJob).toList();
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }

            }
        }
    }

    private ResEmailJob toResEmailJob(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new
                        ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

//    @Scheduled(cron = "*/10 * * * * *")
//    public void testCron(){
//        System.out.println("Test cron job");
//    }
}
