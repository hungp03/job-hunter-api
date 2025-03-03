package vn.hp.jobhunter.service;

import org.springframework.stereotype.Service;
import vn.hp.jobhunter.entity.Job;
import vn.hp.jobhunter.entity.Skill;
import vn.hp.jobhunter.entity.Subscriber;
import vn.hp.jobhunter.dto.response.email.ResEmailJob;
import vn.hp.jobhunter.repository.JobRepository;
import vn.hp.jobhunter.repository.SkillRepository;
import vn.hp.jobhunter.repository.SubscriberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository, JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public Subscriber findByEmail(String email){
        return this.subscriberRepository.findByEmail(email);
    }

    public boolean isEmailExisted(String email){
        return this.subscriberRepository.existsByEmail(email);
    }
    public Subscriber getSubscriber(long id){
        return this.subscriberRepository.findById(id).orElse(null);
    }
    public Subscriber create(Subscriber subscriber){
        //check skill is valid
        if (subscriber.getSkills() != null){
            List<Long> reqSkills = subscriber.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber update(Subscriber subsDB, Subscriber subscriberReq){
        //check skill is valid
        if (subscriberReq.getSkills() != null){
            List<Long> reqSkills = subscriberReq.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subsDB);
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (!listSubs.isEmpty()) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && !listSkills.isEmpty()) {

                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);

                    if (!listJobs.isEmpty()) {
                        List<ResEmailJob> arr = listJobs.stream().map(this::convertJobToSendEmail).toList();
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

    private ResEmailJob convertJobToSendEmail(Job job) {
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
