package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.req.PersistResourceReqDto;
import com.presbo.presboservice.dto.res.AssignedResourceDto;
import com.presbo.presboservice.dto.res.ResourceResDto;
import com.presbo.presboservice.entity.AssignedResource;
import com.presbo.presboservice.entity.Resource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ResourceService {

   void saveResource (Resource resource);

   void persistResource(PersistResourceReqDto data);

   Optional<Resource> findResourceByUniqueIdAndProjectId(Long resourceId, Long projectId);

   Set<Resource> findAllProjectResources(Long projectId);

   boolean deleteResourcesByProject(List<Resource> resource);

   void deleteListOfResourcesByUniqueIdAndProjectId(List<Long> resourceUniqueIds, Long projectId);

   List<ResourceResDto> getAllResourcesByProject(Long projectId);

   Optional<Resource> findResourceById(Long resourceId);

   //Where should this be
   List<AssignedResourceDto> findAssignedResources(String username);

   void removeAllAssignedResourcesByProjectId(Long projectId);

}