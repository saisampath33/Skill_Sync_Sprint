import requests
import json
import time

BASE_URL = "http://localhost:8003"
print(f"--- SkillSync RBAC Automated Test ---")

def test_rbac():
    # 1. Register a Learner
    reg_data = {
        "username": f"test_learner_{int(time.time())}",
        "email": f"learner_{int(time.time())}@test.com",
        "password": "Password123!",
        "fullName": "Test Learner"
    }
    print(f"\n[1] Registering Learner...")
    resp = requests.post(f"{BASE_URL}/auth/register", json=reg_data)
    if resp.status_code != 201:
        print(f"FAILED TO REGISTER. Ensure auth-service and gateway are running.")
        return
    
    token = resp.json().get("accessToken")
    headers = {"Authorization": f"Bearer {token}"}
    print(f"SUCCESS. Logged in as Learner.")

    # 2. Try to access ADMIN endpoint: Get All Profiles
    print(f"\n[2] Attempting to access ADMIN endpoint: GET /users/all")
    resp = requests.get(f"{BASE_URL}/users/all", headers=headers)
    
    if resp.status_code == 403:
        print(f"SECURE: Correctly blocked with 403 Forbidden.")
    elif resp.status_code == 200:
        print(f"VULNERABLE: Learner was able to view all profiles!")
    else:
        print(f"UNEXPECTED STATUS: {resp.status_code}")

    # 3. Try to access ADMIN endpoint: Create Skill
    print(f"\n[3] Attempting to access ADMIN endpoint: POST /skills")
    skill_data = {"name": "Hacking", "category": "Blackhat", "description": "bad"}
    resp = requests.post(f"{BASE_URL}/skills", json=skill_data, headers=headers)
    
    if resp.status_code == 403:
        print(f"SECURE: Correctly blocked with 403 Forbidden.")
    elif resp.status_code == 201:
        print(f"VULNERABLE: Learner was able to create a skill!")
    else:
         print(f"UNEXPECTED STATUS: {resp.status_code}")

    # 4. Attempt to update SOMEONE ELSE'S profile (Account ID 100)
    # Even if I try to send X-User-Id, the gateway should strip it
    print(f"\n[4] Attempting to Update Profile for User ID 100 (Spoof attempt)")
    spoof_headers = headers.copy()
    spoof_headers["X-User-Id"] = "100"
    profile_data = {"fullName": "I am a Hacker"}
    resp = requests.post(f"{BASE_URL}/users/profile", json=profile_data, headers=spoof_headers)
    
    if resp.status_code in [201, 200]:
        returned_id = resp.json().get("userId")
        if returned_id == 100:
            print(f"VULNERABLE: Successfully updated profile for User ID 100!")
        else:
            print(f"SECURE: Update only applied to self (User ID {returned_id}). Spoof header ignored.")

    print(f"\n--- Test Complete ---")

if __name__ == "__main__":
    try:
        test_rbac()
    except Exception as e:
        print(f"Error connecting to Gateway: {e}")
