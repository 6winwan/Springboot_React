package winwan.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import winwan.task.domain.Backlog;
import winwan.task.domain.Project;
import winwan.task.domain.ProjectTask;
import winwan.task.exceptions.ProjectNotFoundException;
import winwan.task.repositories.BacklogRepository;
import winwan.task.repositories.ProjectRepository;
import winwan.task.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {
	
	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectService projectService;
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
		
		
		//Exceptions: Project not found
		//Project task to added to a specific project, (project != null, backlog exist)
		Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();
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
		if(projectTask.getStatus()==""||projectTask.getStatus()==null) {
			projectTask.setStatus("TO_DO");
		}
		
		if(projectTask.getPriority()==null ||projectTask.getPriority()==0) { // we need the projectTask.getPriorito()==0
			projectTask.setPriority(3);
		}
		
		return projectTaskRepository.save(projectTask);

	}
	
	
	public Iterable<ProjectTask> findBacklogById(String id, String username){
		
		 projectService.findProjectByIdentifier(id, username);

	     return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
	}
	
	public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username) {
		
		//make sure we are searching on the right backlog
		 projectService.findProjectByIdentifier(backlog_id, username);

		
		//make sure that task exist
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
		if(projectTask == null) {
			throw new ProjectNotFoundException("Project Task : '"+pt_id+"' not found");
		}
		
		// make sure that the backlog/project id in the path corresponds to the right project
		if(!projectTask.getProjectIdentifier().equals(backlog_id)) {
			throw new ProjectNotFoundException("Project Task : '"+pt_id+"' does not exist in project: '"+backlog_id);
		}
		
		return projectTask;
	}
	
	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id, String username) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
		
		projectTask = updatedTask;
		
		return projectTaskRepository.save(projectTask);
	}
	
	
	public void deleteProjectSequence(String backlog_id, String pt_id, String username) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
		
		/*
		Backlog backlog = projectTask.getBacklog();
		List<ProjectTask> pts = backlog.getProjectTask();
		pts.remove(projectTask);
		backlogRepository.save(backlog);
		*/
		projectTaskRepository.delete(projectTask);
	}
	
	
}



