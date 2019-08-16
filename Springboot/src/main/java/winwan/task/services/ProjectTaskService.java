package winwan.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import winwan.task.domain.Backlog;
import winwan.task.domain.ProjectTask;
import winwan.task.repositories.BacklogRepository;
import winwan.task.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {
	
	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
		
		//Exceptions: Project not found
		
		//Project task to added to a specific project, (project != null, backlog exist)
		Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
		//set the backlog to project task
		projectTask.setBacklog(backlog);
		
		//Set the project sequence to be like : IDPRO-1, IDPRO-2 ... 100
		Integer backlogSequence = backlog.getPTSequence();
		//Update the Backlog Sequence
		backlogSequence++;
		
		backlog.setPTSequence(backlogSequence);
		
		//Add Sequence to Project Task
		projectTask.setProjectSequence(backlog.getProjectIdentifier()+"-"+backlogSequence);
		projectTask.setProjectIdentifier(projectIdentifier);
		
		//Initial priority when priority is null
		
		if(projectTask.getPriority()==null) { // we need the projectTask.getPriorito()==0
			projectTask.setPriority(3);
		}
		
		//Initial Status when status is null
		if(projectTask.getStatus()==""||projectTask.getStatus()==null) {
			projectTask.setStatus("To_Do");
		}
		
		return projectTaskRepository.save(projectTask);
	}
	
	public Iterable<ProjectTask> findBacklogById(String id){
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
	}
}
