/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.stockmanagement.service.referencedata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.openlmis.stockmanagement.dto.referencedata.ResultDto;
import org.openlmis.stockmanagement.dto.referencedata.UserDto;
import org.openlmis.stockmanagement.service.ServiceResponse;
import org.openlmis.stockmanagement.util.RequestParameters;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class UserReferenceDataService extends BaseReferenceDataService<UserDto> {

  public static final String SEARCH = "search";

  @Override
  protected String getUrl() {
    return "/api/users/";
  }

  @Override
  protected Class<UserDto> getResultClass() {
    return UserDto.class;
  }

  @Override
  protected Class<UserDto[]> getArrayResultClass() {
    return UserDto[].class;
  }

  public Collection<UserDto> findUsers(Map<String, Object> parameters) {
    return findAll(SEARCH, parameters);
  }

  /**
   * This method retrieves a list of users whose usernames match with given username.
   *
   * @param name the name of user.
   * @return UserDtos containing user's data, or null if such user was not found.
   */
  public List<UserDto> findUsers(String name) {
    Map<String, Object> payload = Collections.singletonMap("username", name);

    Page<UserDto> users = getPage(SEARCH, Collections.emptyMap(), payload);
    return users.getContent().isEmpty() ? null : users.getContent();
  }

  /**
   * This method retrieves a user with given name.
   *
   * @param name the name of user.
   * @return UserDto containing user's data, or null if such user was not found.
   */
  public UserDto findUser(String name) {
    Map<String, Object> payload = Collections.singletonMap("username", name);

    Page<UserDto> users = getPage("search", Collections.emptyMap(), payload);
    return users.getContent().isEmpty() ? null : users.getContent().get(0);
  }

  /**
   * This method retrieves a user with id.
   *
   * @param id the name of user.
   * @return UserDto containing user's data, or null if such user was not found.
   */
  public UserDto findUser(UUID id) {
    // Wrap the UUID in a collection (Set or List)
    Set<UUID> ids = new HashSet<>();
    ids.add(id);

    // Use the collection as the value for the "id" parameter
    Map<String, Object> payload = Collections.singletonMap("id", ids);

    Page<UserDto> users = getPage("search", Collections.emptyMap(), payload);
    return users.getContent().isEmpty() ? null : users.getContent().get(0);
  }

  /**
   * Check if user has a right with certain criteria.
   *
   * @param user     id of user to check for right
   * @param right    right to check
   * @param program  program to check (for supervision rights, can be
   *                 {@code null})
   * @param facility facility to check (for supervision rights, can be
   *                 {@code null})
   * @return {@link ResultDto} of true or false depending on if user has the
   *         right.
   */
  public ResultDto<Boolean> hasRight(UUID user, UUID right, UUID program, UUID facility,
      UUID warehouse) {
    RequestParameters parameters = RequestParameters
        .init()
        .set("rightId", right)
        .set("programId", program)
        .set("facilityId", facility)
        .set("warehouseId", warehouse);

    return getResult(user + "/hasRight", parameters, Boolean.class);
  }

  public ServiceResponse<List<String>> getPermissionStrings(UUID user, String etag) {
    return tryFindAll(user + "/permissionStrings", String[].class, etag);
  }

  /**
   * Searches users by the given right ID, program ID, supervisory node ID and
   * warehouse ID.
   *
   * @param right           (required) the right UUID
   * @param program         (optional) the program UUID
   * @param supervisoryNode (optional) the supervisory node UUID
   * @param warehouse       (optional) the warehouse UUID
   * @return the list of all matching users
   */
  public Collection<UserDto> findAllUsers(UUID right, UUID program,
      UUID supervisoryNode, UUID warehouse) {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("rightId", right);
    paramMap.put("programId", program);
    paramMap.put("supervisoryNodeId", supervisoryNode);
    paramMap.put("warehouseId", warehouse);

    return findAll("/rightSearch", paramMap);
  }

  public Collection<UserDto> search(RequestParameters parameters) {
    return getPage(parameters).getContent();
  }

}
