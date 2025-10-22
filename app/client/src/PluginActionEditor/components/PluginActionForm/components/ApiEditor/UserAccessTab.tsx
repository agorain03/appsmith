import React, { useEffect, useState } from "react";
import { Button, Checkbox, Text } from "@appsmith/ads";
import Api from "api/Api";
// import { toast } from "react-toastify";
import styled from "styled-components";
import { toast } from "@appsmith/ads";

const Container = styled.div`
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

const GroupsList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 300px;
  overflow-y: auto;
`;

const GroupItem = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
`;

const ButtonContainer = styled.div`
  display: flex;
  gap: 8px;
  margin-top: 16px;
`;

interface UserAccessTabProps {
  isChangePermitted: boolean;
  actionId?: string;
}

const UserAccessTab: React.FC<UserAccessTabProps> = ({ isChangePermitted, actionId }) => {
  const [availableGroups, setAvailableGroups] = useState<string[]>([]);
  const [selectedGroups, setSelectedGroups] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // Fetch available ACL groups
  const fetchAvailableGroups = async () => {
    try {
      setLoading(true);
      const response = await Api.get("/v1/acl/groups");
      if (response.data ) {
        setAvailableGroups(response.data);
      }
    } catch (error) {
      console.error("Error fetching ACL groups:", error);
      toast.show("Failed to update user access", {
      kind: "error",
      autoClose: 3000,
        });
    } finally {
      setLoading(false);
    }
  };

  // Fetch already selected groups for this API
  const fetchSelectedGroups = async () => {
    if (!actionId) return;
    
    try {
      const response = await Api.get(`/v1/acl/actions/${actionId}`);
      if (response.data) {
        setSelectedGroups(response.data);
      }
    } catch (error) {
      console.error("Error fetching selected groups:", error);
      // Don't show error toast as this might be expected for new APIs
    }
  };

  // Save selected groups
  const saveSelectedGroups = async () => {
    if (!actionId) return;
    
    try {
      setSaving(true);
      await Api.put(`/v1/acl/actions/${actionId}`, {
        groups: selectedGroups
      });
      toast.show("User access updated successfully", {
      kind: "success",
      autoClose: 3000,
        });
    } catch (error) {
      console.error("Error saving groups:", error);
      toast.show("Failed to update user access", {
      kind: "error",
      autoClose: 3000,
        });
    } finally {
      setSaving(false);
    }
  };

  // Handle group selection toggle
  const toggleGroup = (group: string) => {
    if (!isChangePermitted) return;
    
    setSelectedGroups(prev => 
      prev.includes(group)
        ? prev.filter(g => g !== group)
        : [...prev, group]
    );
  };

  useEffect(() => {
    fetchAvailableGroups();
    fetchSelectedGroups();
  }, [actionId]);

  if (loading) {
    return <Container><Text>Loading ACL groups...</Text></Container>;
  }

  return (
    <Container>
      <Text kind="heading-m" renderAs="h3">
        User Access Control Groups
      </Text>
      <Text kind="heading-s" color="var(--ads-v2-color-fg-muted)">
        Select which user groups can access this API
      </Text>
      
      <GroupsList>
        {availableGroups.map(group => (
          <GroupItem key={group}>
            <Checkbox
              isSelected={selectedGroups.includes(group)}
              onChange={() => toggleGroup(group)}
              isDisabled={!isChangePermitted}
            >
              {group}
            </Checkbox>
          </GroupItem>
        ))}
      </GroupsList>

      {availableGroups.length === 0 && (
        <Text kind="heading-m" color="var(--ads-v2-color-fg-muted)">
          No ACL groups available
        </Text>
      )}

      <ButtonContainer>
        <Button
          onClick={saveSelectedGroups}
          isLoading={saving}
          isDisabled={!isChangePermitted}
          size="md"
        >
          Save User Access
        </Button>
        <Button
          onClick={fetchSelectedGroups}
          kind="secondary"
          size="md"
          isDisabled={loading}
        >
          Reset
        </Button>
      </ButtonContainer>
    </Container>
  );
};

export default UserAccessTab;