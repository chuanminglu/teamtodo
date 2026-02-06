#!/bin/bash
# Manual API Test Script for Member Management

API_BASE_URL="http://localhost:8080/api"

echo "======================================"
echo "Member Management API Test Script"
echo "======================================"
echo ""
echo "Prerequisites:"
echo "1. MySQL server is running"
echo "2. Database schema is initialized"
echo "3. Backend application is running"
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Add a member
echo -e "${YELLOW}Test 1: Add Member to Project${NC}"
echo "POST ${API_BASE_URL}/projects/members"
curl -X POST "${API_BASE_URL}/projects/members" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "userId": 2,
    "role": "MEMBER"
  }' 2>/dev/null | jq '.' || echo "Please install jq for formatted output"
echo ""
echo ""

# Test 2: Get project members
echo -e "${YELLOW}Test 2: Get Project Members${NC}"
echo "GET ${API_BASE_URL}/projects/1/members"
curl -X GET "${API_BASE_URL}/projects/1/members" 2>/dev/null | jq '.' || echo "Please install jq for formatted output"
echo ""
echo ""

# Test 3: Check membership
echo -e "${YELLOW}Test 3: Check User Membership${NC}"
echo "GET ${API_BASE_URL}/projects/1/members/check?userId=2"
curl -X GET "${API_BASE_URL}/projects/1/members/check?userId=2" 2>/dev/null | jq '.' || echo "Please install jq for formatted output"
echo ""
echo ""

# Test 4: Try to add duplicate member (should fail)
echo -e "${YELLOW}Test 4: Add Duplicate Member (Expected to Fail)${NC}"
echo "POST ${API_BASE_URL}/projects/members"
curl -X POST "${API_BASE_URL}/projects/members" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "userId": 2,
    "role": "MEMBER"
  }' 2>/dev/null | jq '.' || echo "Please install jq for formatted output"
echo ""
echo ""

# Test 5: Remove member (need to provide actual member ID from previous response)
echo -e "${YELLOW}Test 5: Remove Member${NC}"
echo "DELETE ${API_BASE_URL}/projects/1/members/2?requestUserId=1"
echo "Note: Replace member ID (2) with actual ID from 'Get Members' response"
curl -X DELETE "${API_BASE_URL}/projects/1/members/2?requestUserId=1" 2>/dev/null | jq '.' || echo "Please install jq for formatted output"
echo ""
echo ""

echo -e "${GREEN}API test script completed!${NC}"
echo ""
echo "To run backend: mvn spring-boot:run"
echo "To run frontend: cd frontend && npm run dev"
