package org.adaschool.api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.adaschool.api.exception.UserNotFoundException;
import org.adaschool.api.repository.user.User;
import org.adaschool.api.repository.user.UserDto;
import org.adaschool.api.service.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users/")
public class UsersController {

    private final UsersService usersService;

    public UsersController(@Autowired UsersService usersService) {
        this.usersService = usersService;
    }
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createUser = usersService.save(user);
        URI createdUserUri = URI.create("/v1/users/" + createUser.getId());
        return ResponseEntity.created(createdUserUri).body(createUser);
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users")
    @ApiResponse(responseCode = "200", description = "List of users")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = usersService.all();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        User user = usersService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return ResponseEntity.ok(user);
    }


    @Operation(summary = "Update a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody UserDto userDto) {
        try {
            Optional<User> optionalUser = usersService.findById(id);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                existingUser.setName(userDto.getName());
                existingUser.setLastName(userDto.getLastName());
                usersService.save(existingUser);  // Guardar el usuario actualizado
                return ResponseEntity.ok(existingUser);
            } else {
                throw new UserNotFoundException(id);
            }
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UserNotFoundException(id);
        }
    }

    @Operation(summary = "Delete a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
        Optional<User> optionalUser = usersService.findById(id);

        if (optionalUser.isEmpty()) {
            // User not found, throw a UserNotFoundException
            throw new UserNotFoundException(id);
        }

        // User found, delete and return 200 status
        usersService.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
