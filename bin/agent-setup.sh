#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

# Set up AI coding agent integration with TinkerPop's Agent Skills.
#
# TinkerPop maintains development guidance as an Agent Skill in .skills/tinkerpop-dev/.
# Different AI coding tools discover skills in different directories. This script
# creates the necessary symlinks or shims so your tool can find the skill.
#
# Usage:
#   bin/agent-setup.sh <agent>
#   bin/agent-setup.sh --list
#   bin/agent-setup.sh --all
#
# Examples:
#   bin/agent-setup.sh claude       # Set up for Claude Code
#   bin/agent-setup.sh kiro         # Set up for Kiro
#   bin/agent-setup.sh --all        # Set up for all supported agents
#
# Supported agents:
#   claude    - Claude Code (.claude/skills/)
#   copilot   - GitHub Copilot (.github/skills/ and .agents/skills/)
#   cursor    - Cursor (.cursor/skills/)
#   codex     - OpenAI Codex (.codex/skills/)
#   junie     - JetBrains Junie (.junie/skills/)
#   kiro      - Kiro (.kiro/skills/)

set -uo pipefail

SKILL_DIR=".skills/tinkerpop-dev"
SKILL_NAME="tinkerpop-dev"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m'

ok()   { echo -e "  ${GREEN}✓${NC} $1"; }
skip() { echo -e "  ${YELLOW}○${NC} $1"; }
bad()  { echo -e "  ${RED}✗${NC} $1"; }

usage() {
    echo "Usage: bin/agent-setup.sh <agent|--list|--all>"
    echo ""
    echo "Agents: claude, copilot, cursor, codex, junie, kiro"
    echo ""
    echo "Options:"
    echo "  --list    List supported agents and their skill discovery paths"
    echo "  --all     Set up shims for all supported agents"
    echo "  --help    Show this message"
}

# Verify we're in the repo root
if [[ ! -d "$SKILL_DIR" ]]; then
    bad "Cannot find $SKILL_DIR — run this script from the TinkerPop repository root."
    exit 1
fi

# Create a symlink from a tool's skill directory to our canonical skill
setup_symlink() {
    local tool_name="$1"
    local target_dir="$2"

    mkdir -p "$target_dir"
    local link_path="$target_dir/$SKILL_NAME"

    if [[ -L "$link_path" ]]; then
        skip "$tool_name: symlink already exists at $link_path"
        return 0
    fi

    if [[ -e "$link_path" ]]; then
        bad "$tool_name: $link_path already exists and is not a symlink — skipping"
        return 1
    fi

    # Compute relative path from target_dir to SKILL_DIR
    local rel_path
    rel_path=$(python3 -c "import os.path; print(os.path.relpath('$SKILL_DIR', '$target_dir'))" 2>/dev/null)
    if [[ -z "$rel_path" ]]; then
        # Fallback if python3 not available
        rel_path=$(perl -e 'use File::Spec; print File::Spec->abs2rel("'"$SKILL_DIR"'", "'"$target_dir"'")' 2>/dev/null)
    fi
    if [[ -z "$rel_path" ]]; then
        bad "$tool_name: could not compute relative path (need python3 or perl)"
        return 1
    fi

    ln -s "$rel_path" "$link_path"
    ok "$tool_name: created symlink $link_path -> $rel_path"
}

# Kiro doesn't follow symlinks in .kiro/skills/, so we copy the skill directory
# instead. See: https://github.com/kirodotdev/Kiro/issues (symlink support).
setup_kiro() {
    local target_dir=".kiro/skills/$SKILL_NAME"

    if [[ -d "$target_dir" ]]; then
        rm -rf "$target_dir"
        skip "kiro: removed existing copy at $target_dir"
    fi

    mkdir -p ".kiro/skills"
    cp -r "$SKILL_DIR" "$target_dir"
    ok "kiro: copied skill to $target_dir"
    echo ""
    echo -e "  ${YELLOW}NOTE:${NC} Kiro uses a copy, not a symlink. If you update the skill in"
    echo -e "        $SKILL_DIR, re-run this script to sync the changes."
}

setup_agent() {
    local agent="$1"
    case "$agent" in
        claude)
            setup_symlink "claude" ".claude/skills"
            ;;
        copilot)
            setup_symlink "copilot (.github)" ".github/skills"
            setup_symlink "copilot (.agents)" ".agents/skills"
            ;;
        cursor)
            setup_symlink "cursor" ".cursor/skills"
            ;;
        codex)
            setup_symlink "codex" ".codex/skills"
            ;;
        junie)
            setup_symlink "junie" ".junie/skills"
            ;;
        kiro)
            setup_kiro
            ;;
        *)
            bad "Unknown agent: $agent"
            echo ""
            usage
            return 1
            ;;
    esac
}

list_agents() {
    echo "Supported agents and their skill discovery paths:"
    echo ""
    echo "  claude    .claude/skills/$SKILL_NAME/     -> symlink to $SKILL_DIR"
    echo "  copilot   .github/skills/$SKILL_NAME/     -> symlink to $SKILL_DIR"
    echo "            .agents/skills/$SKILL_NAME/     -> symlink to $SKILL_DIR"
    echo "  cursor    .cursor/skills/$SKILL_NAME/     -> symlink to $SKILL_DIR"
    echo "  codex     .codex/skills/$SKILL_NAME/      -> symlink to $SKILL_DIR"
    echo "  junie     .junie/skills/$SKILL_NAME/      -> symlink to $SKILL_DIR"
    echo "  kiro      .kiro/skills/$SKILL_NAME/       -> copy of $SKILL_DIR (re-run to sync)"
}

# --- Main ---

if [[ $# -eq 0 ]]; then
    usage
    exit 1
fi

case "$1" in
    --help|-h)
        usage
        ;;
    --list)
        list_agents
        ;;
    --all)
        echo "Setting up all agent integrations..."
        echo ""
        for agent in claude copilot cursor codex junie kiro; do
            setup_agent "$agent"
        done
        echo ""
        echo "Done. Symlinked directories and generated files are gitignored."
        echo "Add them to .gitignore if they aren't already."
        ;;
    *)
        echo "Setting up $1..."
        echo ""
        setup_agent "$1"
        ;;
esac
