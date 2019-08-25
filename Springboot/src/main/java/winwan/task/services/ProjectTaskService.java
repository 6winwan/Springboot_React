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
	
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
		
		
		//Exceptions: Project not found
		try {
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
			if(projectTask.getStatus()==""||projectTask.getStatus()==null) {
				projectTask.setStatus("TO_DO");
			}
			
			if(projectTask.getPriority()==0 ||projectTask.getPriority()==null) { // we need the projectTask.getPriorito()==0
				projectTask.setPriority(3);
			}
			
			//Initial Status when status is null
			
			
			return projectTaskRepository.save(projectTask);
		} catch (Exception e) {
			throw new ProjectNotFoundException("Project not Found");
		}	
	}
	
	
	public Iterable<ProjectTask> findBacklogById(String id){
		
		Project project = projectRepository.findByProjectIdentifier(id);
		
		if(project == null) {
			throw new ProjectNotFoundException("Project with ID: '"+id+"' does not exist");
		}
		
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
	}
	
	public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {
		
		//make sure we are searching on the right backlog
		Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
		if(backlog == null) {
			throw new ProjectNotFoundException("Project with ID: '"+backlog_id+"' does not exist");
		}
		
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
	
	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
		
		projectTask = updatedTask;
		
		return projectTaskRepository.save(projectTask);
	}
	
	
	public void deleteProjectSequence(String backlog_id, String pt_id) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
		
		/*
		Backlog backlog = projectTask.getBacklog();
		List<ProjectTask> pts = backlog.getProjectTask();
		pts.remove(projectTask);
		backlogRepository.save(backlog);
		*/
		projectTaskRepository.delete(projectTask);
	}
	
	
}



