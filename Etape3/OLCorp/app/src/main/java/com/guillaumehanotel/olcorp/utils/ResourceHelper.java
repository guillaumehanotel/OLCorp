package com.guillaumehanotel.olcorp.utils;

import android.util.Log;

import com.guillaumehanotel.olcorp.beans.Group;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.beans.User;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class ResourceHelper {


    private ResourceHelper(){


    }


    private static ResourceHelper instance;

    public static ResourceHelper getInstance(){
        if(instance == null){
            instance = new ResourceHelper();
        }
        return instance;
    }


    public static boolean isGroupBelongToOU(Group group, OrganizationUnit organizationUnit){
        String ouOfGroup = StringUtils.substringBetween(group.getDistinguishedName(), ",OU=", ",DC=");
        return organizationUnit.getName().equals(ouOfGroup);
    }

    public static ArrayList<Group> filterGroupsByOU(ArrayList<Group> groups, OrganizationUnit currentOrganizationUnit){
        ArrayList<Group> groups_filtered = new ArrayList<>();
        for (Group group : groups) {
            if (isGroupBelongToOU(group, currentOrganizationUnit)) {
                groups_filtered.add(group);
            }
        }
        return groups_filtered;
    }


    public static boolean isUserBelongToGroup(User user, Group group) {
        String groupOfUser = StringUtils.substringBetween(user.getMemberOf(), "CN=", ",OU=");
        if (groupOfUser == null){
            groupOfUser = StringUtils.substringBetween(user.getMemberOf(), "CN=", ",CN=");
        }

        return group.getName().equals(groupOfUser);
    }

    public static ArrayList<User> filterUsersByGroup(ArrayList<User> users, Group group) {
        ArrayList<User> users_filtered = new ArrayList<>();
        for (User user : users) {
            if (isUserBelongToGroup(user, group)) {
                users_filtered.add(user);
            }
        }
        return users_filtered;
    }

    public static boolean isUserBelongToOrganizationUnit(User user, OrganizationUnit organizationUnit){
        String ouOfUser = StringUtils.substringBetween(user.getDistinguishedName(), ",OU=", ",DC=");
        return organizationUnit.getName().equals(ouOfUser);
    }

    public static ArrayList<User> filterUsersByOU(ArrayList<User> users, OrganizationUnit organizationUnit) {
        ArrayList<User> users_filtered = new ArrayList<>();
        for (User user : users) {
            if (isUserBelongToOrganizationUnit(user, organizationUnit)) {
                users_filtered.add(user);
            }
        }
        return users_filtered;
    }


    public static boolean isOrganizationUnitDeletable(OrganizationUnit organizationUnit, ArrayList<Group> groups, ArrayList<User> users){

        // Liste des groupes & users qui appartient à l'OU
        ArrayList<User> usersOfOU = filterUsersByOU(users, organizationUnit);
        ArrayList<Group> groupsOfOU = filterGroupsByOU(groups, organizationUnit);

        // si les 2 sont vides -> l'OU est supprimable
        return usersOfOU.isEmpty() && groupsOfOU.isEmpty();
    }


    public static boolean isGroupDeletable(Group group, ArrayList<User> users){
        ArrayList<User> usersOfGroup = filterUsersByGroup(users, group);
        return usersOfGroup.isEmpty();
    }


}
