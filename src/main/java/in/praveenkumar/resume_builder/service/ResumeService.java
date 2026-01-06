package in.praveenkumar.resume_builder.service;

import in.praveenkumar.resume_builder.documents.Resume;
import in.praveenkumar.resume_builder.dto.AuthResponse;
import in.praveenkumar.resume_builder.dto.CreateResumeRequest;
import in.praveenkumar.resume_builder.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;

    private final AuthService authService;

    public Resume createResume(CreateResumeRequest request, Object principalObject) {
        //Step 1: Create resume object
        Resume newResume = new Resume();

        //Step 2: Get the current profile
        AuthResponse response = authService.getProfile(principalObject);

        //Step 3: Update the resume object
        newResume.setUserId(response.getId());
        newResume.setTitle(request.getTitle());

        //Step 4: Set default data for resume
        setDefaultResumeData(newResume);

        //Step 5: save the resume data
        return resumeRepository.save(newResume);
    }

    private void setDefaultResumeData(Resume newResume) {
        newResume.setProfileInfo(new Resume.ProfileInfo());
        newResume.setContactInfo(new Resume.ContactInfo());
        newResume.setWorkExperiences(new ArrayList<>());
        newResume.setEducations(new ArrayList<>());
        newResume.setSkills(new ArrayList<>());
        newResume.setProjects(new ArrayList<>());
        newResume.setCertifications(new ArrayList<>());
        newResume.setLanguages(new ArrayList<>());
        newResume.setInterests(new ArrayList<>());
    }

    public List<Resume> getUserResumes(Object principal) {
        //Step 1: Get the current profile
        AuthResponse response = authService.getProfile(principal);

        //Step 2: Call the repository finder method
        List<Resume> resumes;
        resumes = resumeRepository.findByUserIdOrderByUpdatedAtDesc(response.getId());

        //Step 3: return response
        return resumes;
    }

    public Resume getResumeId(String resumeId, Object principal) {
        //Step 1: Get the current profile
        AuthResponse response = authService.getProfile(principal);

        //Step 2: Call the repository finder method
        Resume existingResume;
        existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId).orElseThrow(()->new RuntimeException("Resume not Found"));
        return existingResume;
    }

    public Resume updateResume(String resumeId, Resume updatedData, Object principal) {
        //Step 1: Get the current profile
        AuthResponse response = authService.getProfile(principal);

        //Step 2: Call the repo finder method
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId).orElseThrow(()->new RuntimeException("Resume not Found to Update"));

        //Step 3: update the new data
        existingResume.setTitle(updatedData.getTitle());
        existingResume.setThumbnailLink(updatedData.getThumbnailLink());
        existingResume.setTemplate(updatedData.getTemplate());
        existingResume.setProfileInfo(updatedData.getProfileInfo());
        existingResume.setContactInfo(updatedData.getContactInfo());
        existingResume.setWorkExperiences(updatedData.getWorkExperiences());
        existingResume.setEducations(updatedData.getEducations());
        existingResume.setSkills(updatedData.getSkills());
        existingResume.setProjects(updatedData.getProjects());
        existingResume.setCertifications(updatedData.getCertifications());
        existingResume.setLanguages(updatedData.getLanguages());
        existingResume.setInterests(updatedData.getInterests());

        //Step 4: save the details into database
        resumeRepository.save(existingResume);

        //Step 5: return result
        return existingResume;
    }

    public void deleteResume(String resumeId, Object principal) {
        // Step 1: Get the current profile
        AuthResponse response = authService.getProfile(principal);
        // Step 2: Call the repository finder method
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId).orElseThrow(()->new RuntimeException("Resume not found to delete..."));

        resumeRepository.delete(existingResume);
    }
}
